package com.nosmoke.nexus_ai.mapper;

import org.springframework.stereotype.Component;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.model.ErrorLog;

@Component
public class ErrorMapper {
    
    
    public ErrorLog toEntity(ErrorRequest errorRequest) {

        ErrorLog errorLog = new ErrorLog();
        errorLog.setApplicationName(errorRequest.applicationName());
        errorLog.setErrorMessage(errorRequest.errorMessage());
        errorLog.setStackTrace(errorRequest.stackTrace());
        errorLog.setEnvironment(errorRequest.environment());
        errorLog.setComponent(errorRequest.component());
        errorLog.setLevel(errorRequest.level());
        return errorLog;

    }

    public ErrorResponse toResponse(ErrorLog errorLog) {
        
        ErrorResponse errorResponse = new ErrorResponse(errorLog.getId(), errorLog.getCreatedAt(),errorLog.getApplicationName() , errorLog.getEnvironment(), errorLog.getStatus(),
        errorLog.getLevel(), errorLog.getComponent(), errorLog.getAiSolution(), errorLog.getAiExplanation());
        return errorResponse;
    
    }
}
