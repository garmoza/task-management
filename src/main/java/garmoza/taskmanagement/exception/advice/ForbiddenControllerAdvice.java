package garmoza.taskmanagement.exception.advice;

import garmoza.taskmanagement.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ForbiddenControllerAdvice {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenException(RuntimeException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("code", HttpStatus.FORBIDDEN.toString());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
