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
public class TaskPutDTO {
    @Positive
    long id;
    @NotBlank
    String title;
    @NotNull
    String description;
    @NotNull
    TaskStatus status;
    @NotNull
    TaskPriority priority;
    @Positive
    long authorId;
    Long performerId;
}
