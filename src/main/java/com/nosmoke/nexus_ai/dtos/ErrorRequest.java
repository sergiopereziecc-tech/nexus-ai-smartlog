package com.nosmoke.nexus_ai.dtos;

import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;

import jakarta.validation.constraints.NotBlank;

public record ErrorRequest(
    
    @NotBlank(message = "Application name is required")
    String applicationName,

    @NotBlank(message = "Error message is required")
    String errorMessage,

    @NotBlank(message = "Stack trace is required")
    String stackTrace,

    Environment environment,

    Component component, 

    Level level) 
    
    {

    
}
