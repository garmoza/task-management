package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;
import garmoza.taskmanagement.entity.Comment;
import garmoza.taskmanagement.entity.Task;
import garmoza.taskmanagement.entity.User;

public interface CommentService {

    Comment saveNewComment(Task task, User author, Comment comment);

    CommentResponseDTO createComment(CommentCreateDTO dto);

    CommentResponseDTO findCommentById(long id);

    void deleteCommentById(long id);
}
