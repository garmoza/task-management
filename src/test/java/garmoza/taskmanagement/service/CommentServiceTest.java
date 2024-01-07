package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.CommentDtoMapper;
import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.entity.*;
import garmoza.taskmanagement.exception.CommentNotFoundException;
import garmoza.taskmanagement.exception.TaskNotFoundException;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.CommentRepository;
import garmoza.taskmanagement.repository.TaskRepository;
import garmoza.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentDtoMapper commentDtoMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private Task task;
    private User author;

    private CommentCreateDTO createDTO;
    private CommentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(3L)
                .email("author@mail.com")
                .password("author")
                .authorities(Set.of("ROLE_ADMIN"))
                .build();

        task = Task.builder()
                .id(2L)
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.LOW)
                .author(author)
                .performer(author)
                .build();

        comment = Comment.builder()
                .id(1L)
                .body("Comment Body")
                .task(task)
                .author(author)
                .build();

        comment = Comment.builder()
                .id(1L)
                .body("Comment Body")
                .build();

        createDTO = CommentCreateDTO.builder()
                .body("Comment Body")
                .taskId(2L)
                .build();
        responseDTO = CommentResponseDTO.builder()
                .id(1L)
                .body("Comment Body")
                .build();
    }

    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;

    @Test
    void saveNewComment() {
        comment.setTask(null);
        comment.setAuthor(null);
        given(commentRepository.save(Mockito.any(Comment.class))).will(i -> i.getArgument(0));

        var savedComment = commentService.saveNewComment(task, author, comment);

        then(commentRepository).should().save(commentArgumentCaptor.capture());
        Comment value = commentArgumentCaptor.getValue();
        assertThat(value).isEqualTo(savedComment);
        assertEquals(task, savedComment.getTask());
        assertEquals(author, savedComment.getAuthor());
    }

    @Test
    void createComment() {
        authenticateAuthor();
        comment.setTask(null);
        comment.setAuthor(null);
        given(commentDtoMapper.toEntity(createDTO)).willReturn(comment);
        given(taskRepository.findById(createDTO.getTaskId())).willReturn(Optional.of(task));
        given(userRepository.findUserByEmail("author@mail.com")).willReturn(Optional.of(author));
        given(commentRepository.save(comment)).willReturn(comment);
        given(commentDtoMapper.toResponseDTO(comment)).willReturn(responseDTO);

        CommentResponseDTO dto = commentService.createComment(createDTO);

        then(commentRepository).should().save(commentArgumentCaptor.capture());
        Comment value = commentArgumentCaptor.getValue();
        assertEquals(task, value.getTask());
        assertEquals(author, value.getAuthor());
        assertThat(dto).isEqualTo(responseDTO);
    }

    @Test
    void createComment_taskNotFound() {
        long taskId = createDTO.getTaskId();
        given(taskRepository.findById(taskId)).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> commentService.createComment(createDTO));
    }

    @Test
    void createComment_authorNotFound() {
        authenticateAuthor();
        long taskId = createDTO.getTaskId();
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(userRepository.findUserByEmail("author@mail.com")).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.createComment(createDTO));
    }

    @Test
    void findCommentById() {
        long commentId = comment.getId();
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentDtoMapper.toResponseDTO(comment)).willReturn(responseDTO);

        CommentResponseDTO returnedDTO = commentService.findCommentById(commentId);

        InOrder inOrder = Mockito.inOrder(commentRepository, commentDtoMapper);
        then(commentRepository).should(inOrder).findById(commentId);
        then(commentDtoMapper).should(inOrder).toResponseDTO(comment);
        inOrder.verifyNoMoreInteractions();

        assertThat(returnedDTO).isEqualTo(responseDTO);
    }

    @Test
    void findCommentById_commentNotFound() {
        long commentId = 1L;
        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.findCommentById(commentId));
    }

    @Test
    void deleteCommentById() {
        long commentId = 1L;
        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteCommentById(commentId);

        then(commentRepository).should().deleteById(commentId);
    }

    private void authenticateAuthor() {
        Authentication a = new UsernamePasswordAuthenticationToken(
                "author@mail.com",
                null,
                Set.of("ROLE_ADMIN").stream().map(SimpleGrantedAuthority::new).toList()
        );
        SecurityContextHolder.getContext().setAuthentication(a);
    }
}