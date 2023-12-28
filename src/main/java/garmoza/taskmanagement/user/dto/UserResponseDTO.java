package garmoza.taskmanagement.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class UserResponseDTO {
    private long id;
    private String email;
    Set<String> authorities;
}
