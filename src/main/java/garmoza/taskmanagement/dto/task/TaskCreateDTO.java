package garmoza.taskmanagement.dto.task;

import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskCreateDTO {
    @NotBlank
    private String title;
    @NotNull
    private String description;
    @NotNull
    private TaskStatus status;
    @NotNull
    private TaskPriority priority;
    @Positive
    private long authorId;
    private Long performerId;
}
