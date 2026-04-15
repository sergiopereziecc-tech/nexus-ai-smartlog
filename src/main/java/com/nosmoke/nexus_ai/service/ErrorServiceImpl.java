package com.nosmoke.nexus_ai.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.mapper.ErrorMapper;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.repository.ErrorLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService{
    
    private final ErrorLogRepository errorLogRepository;
    private final ErrorMapper errorMapper;

    @Override
    public ErrorResponse create(ErrorRequest errorRequest) {
        
        ErrorLog errorLog = errorMapper.toEntity(errorRequest);
        errorLogRepository.save(errorLog);
        return errorMapper.toResponse(errorLog);
        
    } 
    
    @Override
    public Optional<ErrorLog> read(Long id) {
        
        return Optional.empty();
    }
    

}
