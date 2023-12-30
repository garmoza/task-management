package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.UserDtoMapper;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper dtoMapper;

    @Override
    public User saveNewUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User newUser = dtoMapper.toEntity(dto);

        User user = saveNewUser(newUser);

        return dtoMapper.toResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> findAllUsers(Pageable pageable) {
        return userRepository.findAll()
                .stream()
                .map(dtoMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserResponseDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return dtoMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO putUser(UserPutDTO dto) {
        User updatedUser = userRepository.findById(dto.getId())
                .map(user -> {
                    user.setEmail(dto.getEmail());
                    user.setPassword(dto.getRawPassword());
                    user.setAuthorities(dto.getAuthorities());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new UserNotFoundException(dto.getId()));

        return dtoMapper.toResponseDTO(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
