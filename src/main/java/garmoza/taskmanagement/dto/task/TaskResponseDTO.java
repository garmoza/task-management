package garmoza.taskmanagement.dto.task;

import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TaskResponseDTO {
    private long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
}
