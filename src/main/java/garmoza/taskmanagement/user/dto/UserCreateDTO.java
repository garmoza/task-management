package garmoza.taskmanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserCreateDTO {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String rawPassword;
    @NotEmpty
    Set<String> authorities;
}
