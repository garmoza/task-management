package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper {

    public Comment toEntity(CommentCreateDTO dto) {
        return Comment.builder()
                .body(dto.getBody())
                .build();
    }

    public CommentResponseDTO toResponseDTO(Comment entity) {
        if (entity == null) {
            return null;
        }

        return CommentResponseDTO.builder()
                .id(entity.getId())
                .body(entity.getBody())
                .build();
    }
}
