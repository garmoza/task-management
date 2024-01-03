package garmoza.taskmanagement.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentCreateDTO {
    @NotBlank
    private String body;
    @Positive
    long taskId;
    @Positive
    long authorId;
}
