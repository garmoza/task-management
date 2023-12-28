package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.UserDtoMapper;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    @Override
    public User saveNewUser(User user) {
        return null;
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        return null;
    }

    @Override
    public List<UserResponseDTO> findAllUsers(Pageable pageable) {
        return null;
    }

    @Override
    public UserResponseDTO findUserById(Long id) {
        return null;
    }

    @Override
    public UserResponseDTO putUser(UserPutDTO dto) {
        return null;
    }

    @Override
    public void deleteUserById(Long id) {

    }
}
