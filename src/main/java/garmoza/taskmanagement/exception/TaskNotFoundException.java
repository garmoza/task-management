package garmoza.taskmanagement.exception;

public class TaskNotFoundException extends RuntimeException {

    public static final String MESSAGE = "Task with id=%d not found";

    public TaskNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
