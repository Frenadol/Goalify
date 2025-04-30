package com.frenadol.goalify.controllers;

import com.frenadol.goalify.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, Object>> handleUserException(UserException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", ex.getMessage());
        response.put("campo", ex.getCampo());
        response.put("valor", ex.getValor());

        HttpStatus status = getStatusForException(ex);
        response.put("status", status.value());

        return new ResponseEntity<>(response, status);
    }

    private HttpStatus getStatusForException(UserException ex) {
        String exceptionClassName = ex.getClass().getSimpleName();
        return switch (exceptionClassName) {
            case "UserNotFoundException" -> HttpStatus.NOT_FOUND;
            case "UnauthorizedAccessException" -> HttpStatus.FORBIDDEN;
            case "InvalidCredentialsException" -> HttpStatus.UNAUTHORIZED;
            case "DuplicateUserException" -> HttpStatus.CONFLICT;
            case "InvalidUserDataException" -> HttpStatus.BAD_REQUEST;
            case "UserRelationshipException" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}