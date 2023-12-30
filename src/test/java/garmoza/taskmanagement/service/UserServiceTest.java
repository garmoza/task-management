package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.UserDtoMapper;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private User user;
    private UserCreateDTO userCreateDTO;
    private UserResponseDTO userResponseDTO;
    private UserPutDTO userPutDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("pass")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        userCreateDTO = UserCreateDTO.builder()
                .email("test@mail.com")
                .rawPassword("pass")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("test@mail.com")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        userPutDTO = UserPutDTO.builder()
                .id(1L)
                .email("changed@mail.com")
                .rawPassword("changed_pass")
                .authorities(Set.of("ROLE_CHANGED"))
                .build();
    }

    @Test
    void saveNewUser() {
        given(userRepository.save(Mockito.any(User.class))).will(i -> i.getArgument(0));

        var savedUser = userService.saveNewUser(user);

        then(userRepository).should().save(userArgumentCaptor.capture());
        User value = userArgumentCaptor.getValue();
        assertThat(value).isEqualTo(savedUser);
    }

    @Test
    void createUser() {
        given(userDtoMapper.toEntity(userCreateDTO)).willReturn(user);
        given(userRepository.save(user)).willReturn(user);
        given(userDtoMapper.toResponseDTO(user)).willReturn(userResponseDTO);

        UserResponseDTO saved = userService.createUser(userCreateDTO);

        then(userRepository).should().save(user);
        assertThat(saved).isEqualTo(userResponseDTO);
    }

    @Test
    void findAllUsers() {
        var pageable = PageRequest.of(0, 20);
        List<User> userList = List.of(user, user, user);
        given(userRepository.findAll()).willReturn(userList);
        given(userDtoMapper.toResponseDTO(Mockito.any(User.class))).willReturn(userResponseDTO);

        List<UserResponseDTO> responseDTOList = userService.findAllUsers(pageable);

        InOrder inOrder = Mockito.inOrder(userRepository, userDtoMapper);
        then(userRepository).should(inOrder).findAll();
        then(userDtoMapper).should(inOrder, times(3)).toResponseDTO(user);
        inOrder.verifyNoMoreInteractions();

        assertThat(responseDTOList).hasSize(3);
    }

    @Test
    void findUserById() {
        long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userDtoMapper.toResponseDTO(user)).willReturn(userResponseDTO);

        UserResponseDTO returnedDTO = userService.findUserById(userId);

        InOrder inOrder = Mockito.inOrder(userRepository, userDtoMapper);
        then(userRepository).should(inOrder).findById(userId);
        then(userDtoMapper).should(inOrder).toResponseDTO(user);
        inOrder.verifyNoMoreInteractions();

        assertThat(returnedDTO).isEqualTo(userResponseDTO);
    }

    @Test
    void findUserById_userNotFound() {
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void putUser() {
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(userRepository.save(Mockito.any(User.class))).willReturn(user);
        given(userDtoMapper.toResponseDTO(user)).willReturn(userResponseDTO);

        UserResponseDTO updatedDto = userService.putUser(userPutDTO);

        // updated instance before save
        then(userRepository).should().save(userArgumentCaptor.capture());
        User userBeforeSave = userArgumentCaptor.getValue();
        assertEquals(userPutDTO.getId(), userBeforeSave.getId());
        assertEquals(userPutDTO.getEmail(), userBeforeSave.getEmail());
        assertEquals(userPutDTO.getRawPassword(), userBeforeSave.getPassword());
        assertEquals(userPutDTO.getAuthorities(), userBeforeSave.getAuthorities());

        assertThat(updatedDto).isEqualTo(userResponseDTO);
    }

    @Test
    void putUser_userNotFound() {
        given(userRepository.findById(userPutDTO.getId())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.putUser(userPutDTO));
    }

    @Test
    void deleteUserById() {
        long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        then(userRepository).should().deleteById(userId);
    }
}