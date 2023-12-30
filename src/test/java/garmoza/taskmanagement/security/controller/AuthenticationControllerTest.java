package garmoza.taskmanagement.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import garmoza.taskmanagement.security.config.SecurityFilterChainConfig;
import garmoza.taskmanagement.security.service.AuthenticationService;
import garmoza.taskmanagement.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest({AuthenticationController.class})
@Import({SecurityFilterChainConfig.class, JwtService.class})
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private AuthenticationRequest request;

    @BeforeEach
    void setUp() {
        request = AuthenticationRequest.builder()
                .email("test@mail.com")
                .password("pass123")
                .build();
    }

    @Test
    void authenticate() throws Exception {
        String jwt = "encrypted-jwt";
        given(authenticationService.authenticate(Mockito.any(AuthenticationRequest.class))).willReturn(jwt);

        ResultActions response = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string(jwt));
    }

    void authenticate_validation_nulls() throws Exception {
        ResultActions response = mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .contentType("{}")
        );

        response.andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().json("""
                        {
                            "code": "400 BAD_REQUEST",
                            "message": "Invalid Data",
                            "email": "must not be blank",
                            "password": "must not be blank"
                        }
                        """));
    }

    void authenticate_validation_email() throws Exception {
        request.setEmail("not-email");
        var notValid = request;

        ResultActions response = mockMvc.perform(post("/auth")
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
}