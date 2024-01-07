package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.CommentDtoMapper;
import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.entity.Comment;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.User;
import garmoza.taskmanagement.exception.CommentNotFoundException;
import garmoza.taskmanagement.exception.TaskNotFoundException;
import garmoza.taskmanagement.exception.UserNotFoundException;
import garmoza.taskmanagement.repository.CommentRepository;
import garmoza.taskmanagement.repository.TaskRepository;
import garmoza.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentDtoMapper dtoMapper;

    @Override
    public Comment saveNewComment(Task task, User author, Comment comment) {
        comment.setTask(task);
        comment.setAuthor(author);
        return commentRepository.save(comment);
    }

    @Override
    public CommentResponseDTO createComment(CommentCreateDTO dto) {
        Authentication auth = getAuthentication();
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException(dto.getTaskId()));
        User author = userRepository.findUserByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("user with email (%s) not found".formatted(auth.getName())));

        Comment newComment = dtoMapper.toEntity(dto);

        Comment comment = saveNewComment(task, author, newComment);

        return dtoMapper.toResponseDTO(comment);
    }

    @Override
    public CommentResponseDTO findCommentById(long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        return dtoMapper.toResponseDTO(comment);
    }

    @Override
    public void deleteCommentById(long id) {
        commentRepository.deleteById(id);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
