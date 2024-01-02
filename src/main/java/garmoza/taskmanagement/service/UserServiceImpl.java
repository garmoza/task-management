package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.PatchDtoValidator;
import garmoza.taskmanagement.dto.UserDtoMapper;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPatchDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final PatchDtoValidator patchDtoValidator;

    @Override
    public User saveNewUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        User newUser = dtoMapper.toEntity(dto);

        newUser.setPassword(passwordEncoder.encode(dto.getRawPassword()));

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
    public UserResponseDTO findUserById(long id) {
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
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO patchUserById(long id, UserPatchDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        Set<String> allowedAttrs = Set.of("email", "rawPassword", "authorities");

        patchDtoValidator.validate(dto, allowedAttrs);

        // changes the entity based on received attributes only
        if (dto.isPatchedAttr("email")) {
            user.setEmail(dto.getEmail());
        }
        if (dto.isPatchedAttr("rawPassword")) {
            user.setPassword(passwordEncoder.encode(dto.getRawPassword()));
        }
        if (dto.isPatchedAttr("authorities")) {
            user.setAuthorities(dto.getAuthorities());
        }

        return dtoMapper.toResponseDTO(userRepository.save(user));
    }
}
