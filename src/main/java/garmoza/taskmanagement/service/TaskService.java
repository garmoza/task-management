package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {

    Task saveNewTask(User author, User performer, Task task);

    TaskResponseDTO createTask(TaskCreateDTO dto);

    List<TaskResponseDTO> findAllTasks(Long authorId, Long performerId, Pageable pageable);

    TaskResponseDTO findTaskById(long id);

    TaskResponseDTO putTask(TaskPutDTO dto);

    void deleteTaskById(long id);

    TaskResponseDTO patchTask(long id, TaskPatchDTO dto);
}
