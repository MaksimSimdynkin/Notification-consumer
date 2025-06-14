package org.example.notificationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String field = error.getField();
                    String message = switch (field) {
                        case "to" -> "Поле 'to' не может быть пустым";
                        case "subject" -> "Поле 'subject' не может быть пустым";
                        case "text" -> "Поле 'text' не может быть пустым";
                        default -> "Поле '" + field + "' не может быть пустым";
                    };
                    return message;
                })
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new String(errorMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUncaughtException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        String errorMessage = "Произошла внутренняя ошибка сервера: " + ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new String(errorMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }
} 