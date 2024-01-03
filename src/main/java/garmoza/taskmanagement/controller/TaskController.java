package garmoza.taskmanagement.controller;

import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/tasks")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(
            @RequestParam(required = false) @Positive Long authorId,
            @RequestParam(required = false) @Positive Long performerId,
            @ParameterObject Pageable pageable
    ) {
        return taskService.findAllTasks(authorId, performerId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO postTask(@Valid @RequestBody TaskCreateDTO dto) {
        return taskService.createTask(dto);
    }

    @PutMapping
    public TaskResponseDTO putTask(@Valid @RequestBody TaskPutDTO dto) {
        return taskService.putTask(dto);
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable @Positive long id) {
        return taskService.findTaskById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable @Positive long id) {
        taskService.deleteTaskById(id);
    }

    @PatchMapping("/{id}")
    public TaskResponseDTO patchTaskById(@PathVariable @Positive long id, @RequestBody TaskPatchDTO dto) {
        return taskService.patchTask(id, dto);
    }
}
