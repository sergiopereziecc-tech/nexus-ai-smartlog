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
        ErrorLog savedErrorLog = errorLogRepository.save(errorLog);
        return errorMapper.toResponse(savedErrorLog);
        
    } 
    
    @Override
    public ErrorResponse read(Long id) {
        //explicame que hace el map y el orElseThrow en este contexto
        //El método findById devuelve un Optional<ErrorLog>. 
        // El método map se utiliza para transformar el ErrorLog en un ErrorResponse utilizando el errorMapper. 
        // Si el Optional contiene un valor, se aplicará la función de mapeo y se devolverá un Optional<ErrorResponse>. 
        // Si el Optional está vacío (es decir, no se encontró un ErrorLog con el ID proporcionado  ), 
        // entonces el método orElseThrow se ejecutará, lanzando una RuntimeException con el mensaje "Error log not found".

        return errorLogRepository.findById(id).map(errorMapper::toResponse).orElseThrow(() -> new RuntimeException("Error log not found"));
        
        
    }
    

}
