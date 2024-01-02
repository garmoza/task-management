package garmoza.taskmanagement.controller;

import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPatchDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDTO> getAllUsers(@ParameterObject Pageable pageable) {
        return userService.findAllUsers(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO postUser(@Valid @RequestBody UserCreateDTO dto) {
        return userService.createUser(dto);
    }

    @PutMapping
    public UserResponseDTO putUser(@Valid @RequestBody UserPutDTO dto) {
        return userService.putUser(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable @Positive long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable @Positive long id) {
        userService.deleteUserById(id);
    }

    @PatchMapping("/{id}")
    public UserResponseDTO patchUserById(@PathVariable @Positive long id, @RequestBody UserPatchDTO dto) {
        return userService.patchUserById(id, dto);
    }
}
