package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.PatchDtoValidator;
import garmoza.taskmanagement.dto.TaskDtoMapper;
import garmoza.taskmanagement.dto.task.TaskCreateDTO;
import garmoza.taskmanagement.dto.task.TaskPatchDTO;
import garmoza.taskmanagement.dto.task.TaskPutDTO;
import garmoza.taskmanagement.dto.task.TaskResponseDTO;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.TaskPriority;
import garmoza.taskmanagement.entity.TaskStatus;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.TaskNotFoundException;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.TaskRepository;
import garmoza.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskDtoMapper taskDtoMapper;
    @Mock
    private PatchDtoValidator patchDtoValidator;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private User author;
    private User performer;

    private TaskCreateDTO createDTO;
    private TaskResponseDTO responseDTO;
    private TaskPutDTO putDTO;
    private TaskPatchDTO patchDTO;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(2L)
                .email("author@mail.com")
                .password("author")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        performer = User.builder()
                .id(3L)
                .email("performer@mail.com")
                .password("performer")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        task = Task.builder()
                .id(1L)
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .author(author)
                .performer(performer)
                .build();

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

        putDTO = TaskPutDTO.builder()
                .id(1L)
                .title("Updated title")
                .description("Updated description")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.HIGH)
                .authorId(2L)
                .performerId(3L)
                .build();

        patchDTO = new TaskPatchDTO();
        patchDTO.setTitle("Patched title");
        patchDTO.setDescription("Patched description");
        patchDTO.setStatus(TaskStatus.IN_PROGRESS);
        patchDTO.setPriority(TaskPriority.MEDIUM);
        patchDTO.setAuthorId(5L);
        patchDTO.setPerformerId(6L);
    }

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Test
    void saveNewTask() {
        task.setAuthor(null);
        task.setPerformer(null);
        given(taskRepository.save(Mockito.any(Task.class))).will(i -> i.getArgument(0));

        var savedTask = taskService.saveNewTask(author, performer, task);

        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task value = taskArgumentCaptor.getValue();
        assertThat(value).isEqualTo(savedTask);
    }

    @Test
    void createTask() {
        given(taskDtoMapper.toEntity(createDTO)).willReturn(task);
        given(userRepository.findById(createDTO.getAuthorId())).willReturn(Optional.of(author));
        given(userRepository.findById(createDTO.getPerformerId())).willReturn(Optional.of(performer));
        given(taskRepository.save(task)).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO dto = taskService.createTask(createDTO);

        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task value = taskArgumentCaptor.getValue();
        assertThat(value.getAuthor()).isEqualTo(author);
        assertThat(value.getPerformer()).isEqualTo(performer);
        assertThat(dto).isEqualTo(responseDTO);
    }

    @Test
    void createTask_authorNotFound() {
        long authorId = createDTO.getAuthorId();
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(createDTO));
    }

    @Test
    void createTask_performerNotFound() {
        long authorId = createDTO.getAuthorId();
        long performerId = createDTO.getPerformerId();
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(userRepository.findById(performerId)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(createDTO));
    }

    @Test
    void createTask_performerIsNull() {
        createDTO.setPerformerId(null);

        given(taskDtoMapper.toEntity(createDTO)).willReturn(task);
        given(userRepository.findById(createDTO.getAuthorId())).willReturn(Optional.of(author));
        given(taskRepository.save(task)).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO dto = taskService.createTask(createDTO);

        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task value = taskArgumentCaptor.getValue();
        assertThat(value.getAuthor()).isEqualTo(author);
        assertThat(value.getPerformer()).isNull();
        assertThat(dto).isEqualTo(responseDTO);
    }

    @Test
    void findAllTasks() {
        long authorId = 2L;
        long performerId = 3L;
        var pageable = PageRequest.of(0, 20);

        Page<Task> page = new PageImpl<>(List.of(task, task, task));
        given(taskRepository.findAllByAuthor_IdAndPerformer_Id(authorId, performerId, pageable)).willReturn(page);
        given(taskDtoMapper.toResponseDTO(Mockito.any(Task.class))).willReturn(responseDTO);

        List<TaskResponseDTO> responseDTOList = taskService.findAllTasks(authorId, performerId, pageable);

        InOrder inOrder = Mockito.inOrder(taskRepository, taskDtoMapper);
        then(taskRepository).should(inOrder).findAllByAuthor_IdAndPerformer_Id(authorId, performerId, pageable);
        then(taskDtoMapper).should(inOrder, times(3)).toResponseDTO(task);
        inOrder.verifyNoMoreInteractions();

        assertThat(responseDTOList).hasSize(3);
    }

    @Test
    void findAllTask_onlyAuthorId() {
        long authorId = 2L;
        Long performerId = null;
        var pageable = PageRequest.of(0, 20);

        Page<Task> page = new PageImpl<>(List.of(task, task, task));
        given(taskRepository.findAllByAuthor_Id(authorId, pageable)).willReturn(page);
        given(taskDtoMapper.toResponseDTO(Mockito.any(Task.class))).willReturn(responseDTO);

        List<TaskResponseDTO> responseDTOList = taskService.findAllTasks(authorId, performerId, pageable);

        InOrder inOrder = Mockito.inOrder(taskRepository, taskDtoMapper);
        then(taskRepository).should(inOrder).findAllByAuthor_Id(authorId, pageable);
        then(taskDtoMapper).should(inOrder, times(3)).toResponseDTO(task);
        inOrder.verifyNoMoreInteractions();

        assertThat(responseDTOList).hasSize(3);
    }

    @Test
    void findAllTask_onlyPerformerId() {
        Long authorId = null;
        long performerId = 3L;
        var pageable = PageRequest.of(0, 20);

        Page<Task> page = new PageImpl<>(List.of(task, task, task));
        given(taskRepository.findAllByPerformer_Id(performerId, pageable)).willReturn(page);
        given(taskDtoMapper.toResponseDTO(Mockito.any(Task.class))).willReturn(responseDTO);

        List<TaskResponseDTO> responseDTOList = taskService.findAllTasks(authorId, performerId, pageable);

        InOrder inOrder = Mockito.inOrder(taskRepository, taskDtoMapper);
        then(taskRepository).should(inOrder).findAllByPerformer_Id(performerId, pageable);
        then(taskDtoMapper).should(inOrder, times(3)).toResponseDTO(task);
        inOrder.verifyNoMoreInteractions();

        assertThat(responseDTOList).hasSize(3);
    }

    @Test
    void findAllTask_onlyPageable() {
        Long authorId = null;
        Long performerId = null;
        var pageable = PageRequest.of(0, 20);

        Page<Task> page = new PageImpl<>(List.of(task, task, task));
        given(taskRepository.findAll(pageable)).willReturn(page);
        given(taskDtoMapper.toResponseDTO(Mockito.any(Task.class))).willReturn(responseDTO);

        List<TaskResponseDTO> responseDTOList = taskService.findAllTasks(authorId, performerId, pageable);

        InOrder inOrder = Mockito.inOrder(taskRepository, taskDtoMapper);
        then(taskRepository).should(inOrder).findAll(pageable);
        then(taskDtoMapper).should(inOrder, times(3)).toResponseDTO(task);
        inOrder.verifyNoMoreInteractions();

        assertThat(responseDTOList).hasSize(3);
    }

    @Test
    void findTaskById() {
        long taskId = task.getId();
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO returnedDTO = taskService.findTaskById(taskId);

        InOrder inOrder = Mockito.inOrder(taskRepository, taskDtoMapper);
        then(taskRepository).should(inOrder).findById(taskId);
        then(taskDtoMapper).should(inOrder).toResponseDTO(task);
        inOrder.verifyNoMoreInteractions();

        assertThat(returnedDTO).isEqualTo(responseDTO);
    }

    @Test
    void findTaskById_userNotFound() {
        long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(taskId));
    }

    @Test
    void putTask() {
        given(taskRepository.findById(putDTO.getId())).willReturn(Optional.of(task));
        given(userRepository.findById(putDTO.getAuthorId())).willReturn(Optional.of(author));
        given(userRepository.findById(putDTO.getPerformerId())).willReturn(Optional.of(performer));
        given(taskRepository.save(Mockito.any(Task.class))).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO updatedDTO = taskService.putTask(putDTO);

        // updated instance before save
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskBeforeSave = taskArgumentCaptor.getValue();
        assertEquals(putDTO.getTitle(), taskBeforeSave.getTitle());
        assertEquals(putDTO.getDescription(), taskBeforeSave.getDescription());
        assertEquals(putDTO.getStatus(), taskBeforeSave.getStatus());
        assertEquals(putDTO.getPriority(), taskBeforeSave.getPriority());
        assertEquals(author, taskBeforeSave.getAuthor());
        assertEquals(performer, taskBeforeSave.getPerformer());

        assertThat(updatedDTO).isEqualTo(responseDTO);
    }

    @Test
    void putTask_performerIsNull() {
        putDTO.setPerformerId(null);
        given(taskRepository.findById(putDTO.getId())).willReturn(Optional.of(task));
        given(userRepository.findById(putDTO.getAuthorId())).willReturn(Optional.of(author));
        given(taskRepository.save(Mockito.any(Task.class))).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        taskService.putTask(putDTO);

        // updated instance before save
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskBeforeSave = taskArgumentCaptor.getValue();
        assertNull(taskBeforeSave.getPerformer());
    }

    @Test
    void putTask_authorNotFound() {
        given(userRepository.findById(putDTO.getAuthorId())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.putTask(putDTO));
    }

    @Test
    void putTask_performerNotFound() {
        given(userRepository.findById(putDTO.getAuthorId())).willReturn(Optional.of(author));
        given(userRepository.findById(putDTO.getPerformerId())).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.putTask(putDTO));
    }

    @Test
    void putTask_taskNotFound() {
        given(userRepository.findById(putDTO.getAuthorId())).willReturn(Optional.of(author));
        given(userRepository.findById(putDTO.getPerformerId())).willReturn(Optional.of(performer));
        given(taskRepository.findById(putDTO.getId())).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.putTask(putDTO));
    }

    @Test
    void deleteTaskById() {
        long taskId = 1L;
        doNothing().when(taskRepository).deleteById(taskId);

        taskService.deleteTaskById(taskId);

        then(taskRepository).should().deleteById(taskId);
    }

    @Test
    void patchTaskById_ROLE_ADMIN() {
        authenticateAdmin();

        long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        doNothing().when(patchDtoValidator).validate(patchDTO, Set.of(
                "title", "description", "status", "priority", "authorId", "performerId"
        ));
        given(userRepository.findById(patchDTO.getAuthorId())).willReturn(Optional.of(author));
        given(userRepository.findById(patchDTO.getPerformerId())).willReturn(Optional.of(performer));
        given(taskRepository.save(Mockito.any(Task.class))).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO patchedDTO = taskService.patchTaskById(taskId, patchDTO);

        // patched instance before save
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskBeforeSave = taskArgumentCaptor.getValue();
        assertEquals(patchDTO.getTitle(), taskBeforeSave.getTitle());
        assertEquals(patchDTO.getDescription(), taskBeforeSave.getDescription());
        assertEquals(patchDTO.getStatus(), taskBeforeSave.getStatus());
        assertEquals(patchDTO.getPriority(), taskBeforeSave.getPriority());
        assertEquals(author, taskBeforeSave.getAuthor());
        assertEquals(performer, taskBeforeSave.getPerformer());

        assertThat(patchedDTO).isEqualTo(responseDTO);
    }

    @Test
    void patchTaskById_author() {
        authenticateAuthor();

        TaskPatchDTO authorPatch = new TaskPatchDTO();
        authorPatch.setTitle("Patched title");
        authorPatch.setDescription("Patched description");
        authorPatch.setStatus(TaskStatus.IN_PROGRESS);
        authorPatch.setPriority(TaskPriority.MEDIUM);
        authorPatch.setPerformerId(6L);

        long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        doNothing().when(patchDtoValidator).validate(authorPatch, Set.of(
                "title", "description", "status", "priority", "performerId"
        ));
        given(userRepository.findById(authorPatch.getPerformerId())).willReturn(Optional.of(performer));
        given(taskRepository.save(Mockito.any(Task.class))).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO patchedDTO = taskService.patchTaskById(taskId, authorPatch);

        // patched instance before save
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskBeforeSave = taskArgumentCaptor.getValue();
        assertEquals(authorPatch.getTitle(), taskBeforeSave.getTitle());
        assertEquals(authorPatch.getDescription(), taskBeforeSave.getDescription());
        assertEquals(authorPatch.getStatus(), taskBeforeSave.getStatus());
        assertEquals(authorPatch.getPriority(), taskBeforeSave.getPriority());
        assertEquals(task.getAuthor(), taskBeforeSave.getAuthor());
        assertEquals(performer, taskBeforeSave.getPerformer());

        assertThat(patchedDTO).isEqualTo(responseDTO);
    }

    @Test
    void patchTaskById_performer() {
        authenticatePerformer();

        TaskPatchDTO performerPatch = new TaskPatchDTO();
        performerPatch.setStatus(TaskStatus.IN_PROGRESS);

        long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        doNothing().when(patchDtoValidator).validate(performerPatch, Set.of("status"));
        given(taskRepository.save(Mockito.any(Task.class))).willReturn(task);
        given(taskDtoMapper.toResponseDTO(task)).willReturn(responseDTO);

        TaskResponseDTO patchedDTO = taskService.patchTaskById(taskId, performerPatch);

        // patched instance before save
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskBeforeSave = taskArgumentCaptor.getValue();
        assertEquals(task.getTitle(), taskBeforeSave.getTitle());
        assertEquals(task.getDescription(), taskBeforeSave.getDescription());
        assertEquals(performerPatch.getStatus(), taskBeforeSave.getStatus());
        assertEquals(task.getPriority(), taskBeforeSave.getPriority());
        assertEquals(task.getPerformer(), taskBeforeSave.getPerformer());

        assertThat(patchedDTO).isEqualTo(responseDTO);
    }

    @Test
    void patchTaskById_taskNotFound() {
        long taskId = 1L;
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.patchTaskById(taskId, patchDTO));
    }

    private void authenticateAdmin() {
        Authentication a = new UsernamePasswordAuthenticationToken(
                "test@mail.com",
                null,
                Set.of("ROLE_ADMIN").stream().map(SimpleGrantedAuthority::new).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(a);
    }

    private void authenticateAuthor() {
        Authentication a = new UsernamePasswordAuthenticationToken(
                author.getEmail(),
                null,
                Set.of("ROLE_USER").stream().map(SimpleGrantedAuthority::new).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(a);
    }

    private void authenticatePerformer() {
        Authentication a = new UsernamePasswordAuthenticationToken(
                performer.getEmail(),
                null,
                Set.of("ROLE_USER").stream().map(SimpleGrantedAuthority::new).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(a);
    }
}