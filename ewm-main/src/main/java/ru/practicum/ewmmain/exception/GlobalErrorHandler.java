package ru.practicum.ewmmain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalErrorHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleException(BadRequestException e) {
        log.info("Ошибка 500: {}", e.getMessage(), e);

        return new ResponseEntity<>(Map.of("500 Internal Server Error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleException(NotFoundException e) {
        log.info("Ошибка 404: {}", e.getMessage(), e);

        return new ResponseEntity<>(Map.of("404 Not Found", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleException(ConflictException e) {
        log.info("Ошибка 409: {}", e.getMessage(), e);

        return new ResponseEntity<>(Map.of("409 Conflict", e.getMessage()), HttpStatus.CONFLICT);
    }
}