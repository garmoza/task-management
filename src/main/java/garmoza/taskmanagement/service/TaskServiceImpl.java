package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Override
    public Task saveNewTask(User author, User performer, Task task) {
        return null;
    }

    @Override
    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        return null;
    }

    @Override
    public List<TaskResponseDTO> findAllTasks(Long authorId, Long performerId, Pageable pageable) {
        return null;
    }

    @Override
    public TaskResponseDTO findTaskById(long id) {
        return null;
    }

    @Override
    public TaskResponseDTO putTask(TaskPutDTO dto) {
        return null;
    }

    @Override
    public void deleteTaskById(long id) {

    }

    @Override
    public TaskResponseDTO patchTask(long id, TaskPatchDTO dto) {
        return null;
    }
}
