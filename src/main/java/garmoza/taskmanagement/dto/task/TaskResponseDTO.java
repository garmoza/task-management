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
    long id;
    String title;
    String description;
    TaskStatus status;
    TaskPriority priority;
}
