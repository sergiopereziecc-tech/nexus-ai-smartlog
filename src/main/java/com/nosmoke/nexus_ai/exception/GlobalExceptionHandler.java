package com.nosmoke.nexus_ai.exception;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.nosmoke.nexus_ai.dtos.ApiError;
import com.nosmoke.nexus_ai.dtos.ApiValidationError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFound ex, WebRequest request) {

        ApiError apiError = new ApiError(LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)); // False: not reveal ip of the user, just the path

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
    public ResponseEntity<ApiValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            WebRequest request) {
        // Map <key, value>, binding results get a list that we can stream, method
        // reference first key, then value to fill the map
        Map<String, String> infoMap = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing // for duplicates
                ));
        ApiValidationError apiValidationError = new ApiValidationError(LocalDateTime.now(),
                "Validation Failed",
                request.getDescription(false),
                infoMap);

        return ResponseEntity.badRequest().body(apiValidationError);

    }

}
