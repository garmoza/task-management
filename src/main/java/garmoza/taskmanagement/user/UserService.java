package garmoza.taskmanagement.user;

import garmoza.taskmanagement.user.dto.UserCreateDTO;
import garmoza.taskmanagement.user.dto.UserPutDTO;
import garmoza.taskmanagement.user.dto.UserResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User saveNewUser(User user);

    UserResponseDTO createUser(UserCreateDTO dto);

    List<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findUserById(Long id);

    UserResponseDTO putUser(UserPutDTO dto);

    void deleteUserById(Long id);
}
