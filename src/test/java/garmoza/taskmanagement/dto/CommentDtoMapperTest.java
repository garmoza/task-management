package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentDtoMapperTest {

    private CommentDtoMapper dtoMapper;
    private CommentCreateDTO createDTO;
    private CommentResponseDTO responseDTO;
    private Comment entity;

    @BeforeEach
    void setUp() {
        dtoMapper = new CommentDtoMapper();

        createDTO = CommentCreateDTO.builder()
                .body("Comment body")
                .taskId(2L)
                .authorId(3L)
                .build();

        responseDTO = CommentResponseDTO.builder()
                .id(1L)
                .body("Comment body")
                .build();

        entity = Comment.builder()
                .id(1L)
                .body("Comment body")
                .build();
    }

    @Test
    void toEntity() {
        var mappedEntity = dtoMapper.toEntity(createDTO);
        mappedEntity.setId(1L);

        assertThat(mappedEntity)
                .usingRecursiveComparison()
                .isEqualTo(entity);
    }

    @Test
    void toResponseDTO() {
        var mappedDTO = dtoMapper.toResponseDTO(entity);

        assertThat(mappedDTO)
                .usingRecursiveComparison()
                .isEqualTo(responseDTO);
    }
}