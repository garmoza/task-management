package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.dto.user.UserCreateDTO;
import garmoza.taskmanagement.dto.user.UserResponseDTO;
import garmoza.taskmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private UserDtoMapper dtoMapper;
    private UserCreateDTO createDTO;
    private UserResponseDTO responseDTO;
    private User entity;

    @BeforeEach
    void setUp() {
        dtoMapper = new UserDtoMapper();

        createDTO = UserCreateDTO.builder()
                .email("test@mail.com")
                .rawPassword("rawPass")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        responseDTO = UserResponseDTO.builder()
                .id(1L)
                .email("test@mail.com")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        entity = User.builder()
                .id(1L)
                .email("test@mail.com")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();
    }

    @Test
    void toEntity() {
        User mappedEntity = dtoMapper.toEntity(createDTO);
        mappedEntity.setId(1L);

        assertThat(mappedEntity)
                .usingRecursiveComparison()
                .isEqualTo(entity);
    }

    @Test
    void toResponseDTO() {
        UserResponseDTO mappedDTO = dtoMapper.toResponseDTO(entity);

        assertThat(mappedDTO)
                .usingRecursiveComparison()
                .isEqualTo(responseDTO);
    }
}