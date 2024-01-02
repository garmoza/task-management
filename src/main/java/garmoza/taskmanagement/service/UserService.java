package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.user.UserPatchDTO;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    User saveNewUser(User user);

    UserResponseDTO createUser(UserCreateDTO dto);

    List<UserResponseDTO> findAllUsers(Pageable pageable);

    UserResponseDTO findUserById(long id);

    UserResponseDTO putUser(UserPutDTO dto);

    void deleteUserById(long id);

    UserResponseDTO patchUserById(long id, UserPatchDTO dto);
}
