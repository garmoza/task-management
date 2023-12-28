package garmoza.taskmanagement.exception.advice;

import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ValidationControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintValidationException(ConstraintViolationException e) {
        Map<String, String> body = new HashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Invalid Data");
        for (var violation : e.getConstraintViolations()) {
            PathImpl path = (PathImpl) violation.getPropertyPath();
            body.put(path.getLeafNode().getName(), violation.getMessage());
        }
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> body = new HashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "Invalid Data");
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            body.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
