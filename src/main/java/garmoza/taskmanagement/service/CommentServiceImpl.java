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
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException(dto.getTaskId()));
        User author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new UserNotFoundException(dto.getAuthorId()));
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
}
