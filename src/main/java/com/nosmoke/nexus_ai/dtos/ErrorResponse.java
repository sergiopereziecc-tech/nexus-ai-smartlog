package com.nosmoke.nexus_ai.dtos;

import java.time.LocalDateTime;

import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;




public record ErrorResponse(
    
    Long id, 
    LocalDateTime createdAt, 
    String applicationName, 
    Environment environment, 
    Status status,
    Level level, 
    Component component, 
    String aiSolution, 
    String aiExplanation) 
    {
    
}
