package com.nosmoke.nexus_ai.service;

import java.util.List;
import java.util.Optional;



import org.springframework.stereotype.Service;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;


public interface ErrorService {

    ErrorResponse create(ErrorRequest errorRequest);
    ErrorResponse read(Long id);
    List<ErrorLog> readAll();
    void delete(Long id);
    List<ErrorResponse> getByApplicationName(String applicationName);
    List<ErrorResponse> getByStatus(Status status);
    List<ErrorResponse> getByLevel(Level level);
    List<ErrorResponse> getByComponent(Component component);
    List<ErrorResponse> getByEnvironment(Environment environment);


    
}
