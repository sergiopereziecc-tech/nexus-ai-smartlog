package com.nosmoke.nexus_ai.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler
    public ResponseEntity<String> handleResourceNotFoundException(ResourceEx ex){
        

    }
    
}
