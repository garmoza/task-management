package garmoza.taskmanagement.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserPutDTO {
    @Positive
    private long id;
    @Email @NotBlank
    private String email;
    @NotBlank
    private String rawPassword;
    @NotEmpty
    Set<String> authorities;
}
