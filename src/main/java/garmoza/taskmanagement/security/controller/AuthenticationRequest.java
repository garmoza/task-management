package garmoza.taskmanagement.security.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String password;
}
