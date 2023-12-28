package garmoza.taskmanagement.exception.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class HttpMessageConversionControllerAdvice {

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleHttpMessageConversionControllerAdvice(HttpMessageConversionException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("code", HttpStatus.BAD_REQUEST.toString());
        body.put("message", "failed to convert message");
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
