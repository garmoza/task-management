package garmoza.taskmanagement.dto;

import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskDtoMapperTest {

    private TaskDtoMapper dtoMapper;
    private TaskCreateDTO createDTO;
    private TaskResponseDTO responseDTO;
    private Task entity;

    @BeforeEach
    void setUp() {
        dtoMapper = new TaskDtoMapper();

        createDTO = TaskCreateDTO.builder()
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .authorId(2L)
                .performerId(3L)
                .build();

        responseDTO = TaskResponseDTO.builder()
                .id(1L)
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .build();

        entity = Task.builder()
                .id(1L)
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
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