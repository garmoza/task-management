package garmoza.taskmanagement.exception;

public class CommentNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Comment with id=%d not found";

    public CommentNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
