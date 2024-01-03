package garmoza.taskmanagement.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponseDTO {
    long id;
    String body;
}
