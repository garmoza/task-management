package garmoza.taskmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserPutDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.security.service.JwtService;
import garmoza.taskmanagement.service.UserService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({UserController.class})
@AutoConfigureMockMvc(addFilters = false) // disables security
@Import({JwtService.class})
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserCreateDTO createDTO = UserCreateDTO.builder()
            .email("test@mail.com")
            .rawPassword("pass")
            .authorities(Set.of("ROLE_ADMIN"))
            .build();

    private UserResponseDTO responseDTO = UserResponseDTO.builder()
            .id(1L)
            .email("test@mail.com")
            .authorities(Set.of("ROLE_ADMIN"))
            .build();

    private UserPutDTO putDTO = UserPutDTO.builder()
            .id(1L)
            .email("changed@mail.com")
            .rawPassword("changed_pass")
            .authorities(Set.of("ROLE_CHANGED"))
            .build();

    @BeforeEach
    void setUp() {
        createDTO = UserCreateDTO.builder()
                .email("test@mail.com")
                .rawPassword("pass")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("test@mail.com")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        putDTO = UserPutDTO.builder()
                .id(1L)
                .email("changed@mail.com")
                .rawPassword("changed_pass")
                .authorities(Set.of("ROLE_CHANGED"))
                .build();
    }

    private static final String jsonResponseDTO = """
            {
                "id": 1,
                "email": "test@mail.com",
                "authorities": ["ROLE_ADMIN"]
            }
            """;

    @Test
    void getAllUsers() throws Exception {
        var responseDto = List.of(responseDTO);
        var pageable = PageRequest.of(0, 20);
        given(userService.findAllUsers(pageable)).willReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(responseDto.size())));
    }

    @Test
    void postUser() throws Exception {
        given(userService.createUser(Mockito.any(UserCreateDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().json(jsonResponseDTO));
    }

    @Test
    void postUser_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "email": "must not be blank",
                            "rawPassword": "must not be blank",
                            "authorities": "must not be empty"
                        }
                        """));
    }

    @Test
    void postUser_validation_email() throws Exception {
        createDTO.setEmail("not-email");
        var notValid = createDTO;

        ResultActions response = mockMvc.perform(post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValid))
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "email": "must be a well-formed email address"
                        }
                        """));
    }

    @Test
    void putUser() throws Exception {
        given(userService.putUser(Mockito.any(UserPutDTO.class))).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(put("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putDTO))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @Test
    void putUser_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(put("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0",
                            "email": "must not be blank",
                            "rawPassword": "must not be blank",
                            "authorities": "must not be empty"
                        }
                        """));
    }

    @Test
    void putUser_validation_email() throws Exception {
        putDTO.setEmail("not-email");
        var notValid = putDTO;

        ResultActions response = mockMvc.perform(put("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notValid))
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "email": "must be a well-formed email address"
                        }
                        """));
    }

    @Test
    void getUserById() throws Exception {
        long id = 1L;
        given(userService.findUserById(id)).willReturn(responseDTO);

        ResultActions response = mockMvc.perform(get("/users/%d".formatted(id))
                .accept(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(jsonResponseDTO));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void getUserById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(get("/users/%d".formatted(id))
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0"
                        }
                        """));
    }

    @Test
    void deleteUserById() throws Exception {
        long id = 1L;
        doNothing().when(userService).deleteUserById(id);

        ResultActions response = mockMvc.perform(delete("/users/%d".formatted(id)));

        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    void deleteUserById_validation_params(long id) throws Exception {
        ResultActions response = mockMvc.perform(delete("/users/%d".formatted(id))
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "id": "must be greater than 0"
                        }
                        """));
    }
}