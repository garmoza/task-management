package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public User toEntity(UserCreateDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .authorities(dto.getAuthorities())
                .build();
    }

    public UserResponseDTO toResponseDTO(User entity) {
        if (entity == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .authorities(entity.getAuthorities())
                .build();
    }
}
