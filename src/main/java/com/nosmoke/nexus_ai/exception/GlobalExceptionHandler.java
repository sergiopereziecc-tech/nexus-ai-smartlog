package com.nosmoke.nexus_ai.exception;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.nosmoke.nexus_ai.dtos.ApiError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFound ex, WebRequest request) {

        ApiError apiError = new ApiError(LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(Exception ex, WebRequest request) {

        ApiError apiError = new ApiError(LocalDateTime.now(),
                "An internal error occurred. Please contact the administrator.",
                request.getDescription(false));

        return ResponseEntity.internalServerError().body(apiError);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        Map<String, String> infoMap = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing
                ));
        
        return ResponseEntity.badRequest().body(infoMap);

    }

}
