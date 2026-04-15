package com.nosmoke.nexus_ai.service;

import org.springframework.stereotype.Service;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.repository.ErrorLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService{
    
    final ErrorLogRepository errorLogRepository;

    @Override
    public ErrorResponse create(ErrorRequest errorRequest) {
        
    }  
    

}
