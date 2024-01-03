package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.PatchDtoValidator;
import garmoza.taskmanagement.dto.TaskDtoMapper;
import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.TaskNotFoundException;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.TaskRepository;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskDtoMapper dtoMapper;
    private final PatchDtoValidator patchDtoValidator;

    @Override
    public Task saveNewTask(User author, User performer, Task task) {
        task.setAuthor(author);
        task.setPerformer(performer);
        return taskRepository.save(task);
    }

    @Override
    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(dto.getAuthorId()));
        User performer = null;
        if (dto.getPerformerId() != null) {
            performer = userRepository.findById(dto.getPerformerId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getPerformerId()));
        }
        Task newTask = dtoMapper.toEntity(dto);

        Task task = saveNewTask(author, performer, newTask);

        return dtoMapper.toResponseDTO(task);
    }

    @Override
    public List<TaskResponseDTO> findAllTasks(Long authorId, Long performerId, Pageable pageable) {
        Page<Task> tasks;
        if (authorId != null && performerId != null) {
            tasks = taskRepository.findAllByAuthor_IdAndPerformer_Id(authorId, performerId, pageable);
        } else if (authorId != null) {
            tasks = taskRepository.findAllByAuthor_Id(authorId, pageable);
        } else if (performerId != null) {
            tasks = taskRepository.findAllByPerformer_Id(performerId, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }

        return tasks.map(dtoMapper::toResponseDTO).toList();
    }

    @Override
    public TaskResponseDTO findTaskById(long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return dtoMapper.toResponseDTO(task);
    }

    @Override
    public TaskResponseDTO putTask(TaskPutDTO dto) {
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(dto.getAuthorId()));
        final User performer;
        if (dto.getPerformerId() != null) {
            performer = userRepository.findById(dto.getPerformerId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getPerformerId()));
        } else {
            performer = null;
        }

        Task updatedTask = taskRepository.findById(dto.getId())
                .map(task -> {
                    task.setTitle(dto.getTitle());
                    task.setDescription(dto.getDescription());
                    task.setStatus(dto.getStatus());
                    task.setPriority(dto.getPriority());
                    task.setAuthor(author);
                    task.setPerformer(performer);
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new TaskNotFoundException(dto.getId()));

        return dtoMapper.toResponseDTO(updatedTask);
    }

    @Override
    public void deleteTaskById(long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponseDTO patchTaskById(long id, TaskPatchDTO dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));

        Authentication auth = getAuthentication();
        Set<String> allowedAttrs = new HashSet<>();
        if (isAdmin(auth)) {
            allowedAttrs.addAll(Set.of(
                    "title", "description", "status", "priority", "authorId", "performerId"
            ));
        }
        if (isAuthor(task, auth)) {
            allowedAttrs.addAll(Set.of(
                    "title", "description", "status", "priority", "performerId"
            ));
        }
        if (isPerformer(task, auth)) {
            allowedAttrs.add("status");
        }

        patchDtoValidator.validate(dto, allowedAttrs);

        // changes the entity based on received attributes only
        if (dto.isPatchedAttr("title")) {
            task.setTitle(dto.getTitle());
        }
        if (dto.isPatchedAttr("description")) {
            task.setDescription(dto.getDescription());
        }
        if (dto.isPatchedAttr("status")) {
            task.setStatus(dto.getStatus());
        }
        if (dto.isPatchedAttr("priority")) {
            task.setPriority(dto.getPriority());
        }
        if (dto.isPatchedAttr("authorId")) {
            User author = userRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getAuthorId()));
            task.setAuthor(author);
        }
        if (dto.isPatchedAttr("performerId")) {
            User performer = null;
            if (dto.getPerformerId() != null) {
                performer = userRepository.findById(dto.getPerformerId())
                        .orElseThrow(() -> new UserNotFoundException(dto.getPerformerId()));
            }
            task.setPerformer(performer);
        }

        return dtoMapper.toResponseDTO(taskRepository.save(task));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    private boolean isAuthor(Task task, Authentication auth) {
        return task.getAuthor().getEmail().equals(auth.getName());
    }

    private boolean isPerformer(Task task, Authentication auth) {
        return task.getPerformer().getEmail().equals(auth.getName());
    }
}
