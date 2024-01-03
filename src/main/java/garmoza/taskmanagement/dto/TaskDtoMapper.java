package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoMapper {

    public Task toEntity(TaskCreateDTO dto) {
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .build();
    }

    public TaskResponseDTO toResponseDTO(Task entity) {
        if (entity == null) {
            return null;
        }

        return TaskResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .build();
    }
}
