package garmoza.taskmanagement.exception.advice;

import garmoza.taskmanagement.exception.CommentNotFoundException;
import garmoza.taskmanagement.exception.TaskNotFoundException;
import garmoza.taskmanagement.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class NotFoundControllerAdvice {

    @ExceptionHandler({
            UserNotFoundException.class,
            TaskNotFoundException.class,
            CommentNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFoundException(RuntimeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("code", HttpStatus.NOT_FOUND.toString());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
