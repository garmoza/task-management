package garmoza.taskmanagement.exception;

public class UserNotFoundException extends RuntimeException {

    public static final String MESSAGE = "User with id=%d not found";

    public UserNotFoundException(Long id) {
        super(String.format(MESSAGE, id));
    }
}
