package garmoza.taskmanagement.dto.user;

import garmoza.taskmanagement.dto.PatchDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Set;

@Getter
public class UserPatchDTO extends PatchDTO {
    @Email @NotBlank
    private String email;
    @NotBlank
    private String rawPassword;
    @NotEmpty
    private Set<String> authorities;

    public void setEmail(String email) {
        this.email = email;
        addPatchedAttr("email");
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
        addPatchedAttr("rawPassword");
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
        addPatchedAttr("authorities");
    }
}
