package com.nosmoke.nexus_ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.mapper.ErrorMapper;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;
import com.nosmoke.nexus_ai.repository.ErrorLogRepository;
import com.nosmoke.nexus_ai.service.ErrorServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TestErrorServiceImpl {
    
    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private ErrorLogRepository errorLogRepository;

    @InjectMocks
    private ErrorServiceImpl errorServiceImpl;

    ErrorRequest errorRequest = new ErrorRequest("Poster", "Error", "StackTrace", ErrorLog.Environment.DEV, 
        ErrorLog.Component.BACKEND, ErrorLog.Level.INFO);

    ErrorLog errorLog = new ErrorLog(1L, null, "Poster", "Error",
     "StackTrace", null, null, null,
      Environment.DEV, Level.INFO, Status.PENDING, Component.BACKEND);
    
    ErrorResponse errorResponse = new ErrorResponse(1L, null, "Poster", Environment.DEV, 
        Status.PENDING, Level.INFO, Component.BACKEND, null, null);
    

    @Test
    void shouldCreateErrorLog(){
        //Arrange
        when(errorMapper.toEntity(errorRequest)).thenReturn(errorLog);
        when(errorLogRepository.save(errorLog)).thenReturn(errorLog);
        when(errorMapper.toResponse(errorLog)).thenReturn(errorResponse);

        //Act
        ErrorResponse errorResponseTest = errorServiceImpl.create(errorRequest);

        //Assert
        assertEquals(errorResponseTest, errorResponse);
        
    }

    @Test
    void shouldFindErrorLogById(){
        
        when(errorLogRepository.findById(errorLog.getId())).thenReturn(Optional.of(errorLog));
        when(errorMapper.toResponse(errorLog)).thenReturn(errorResponse);

        ErrorResponse errorResponseTest = errorServiceImpl.read(errorLog.getId());

        assertEquals(errorResponseTest, errorResponse);

    }

}
