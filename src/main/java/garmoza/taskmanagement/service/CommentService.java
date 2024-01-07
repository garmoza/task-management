package garmoza.taskmanagement.service;

import garmoza.taskmanagement.dto.comment.CommentCreateDTO;
import garmoza.taskmanagement.dto.comment.CommentResponseDTO;

public interface CommentService {

    CommentResponseDTO createComment(CommentCreateDTO dto);

    CommentResponseDTO findCommentById(long id);

    void deleteCommentById(long id);
}
