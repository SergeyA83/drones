package com.musala.drones.exceptions.handler;

import com.musala.drones.dto.ResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ResponseDto> handle(ResponseStatusException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseDto("error", ex.getReason()), ex.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDto> handle(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);

        var errorsFullMessage = ex.getBindingResult().getAllErrors()
                .stream()
                .map(e -> ((FieldError) e).getField() + " " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(new ResponseDto("error", errorsFullMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDto> handle(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);

        var errorsFullMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(new ResponseDto("error", errorsFullMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDto> handle(AccessDeniedException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseDto("error", "Access decided"), HttpStatus.BAD_REQUEST);
    }
}
