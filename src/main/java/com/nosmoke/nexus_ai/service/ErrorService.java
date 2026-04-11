package com.nosmoke.nexus_ai.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.hibernate.cfg.Environment;
import org.hibernate.mapping.Component;
import org.springframework.stereotype.Service;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;


public interface ErrorService {

    ErrorResponse create(ErrorRequest errorRequest);
    Optional<ErrorLog> read(Long id);
    List<ErrorLog> readAll();
    void delete(Long id);
    List<ErrorLog> getByApplicationName(String applicationName);
    List<ErrorLog> getByStatus(Status status);
    List<ErrorLog> getByLevel(Level level);
    List<ErrorLog> getByComponent(Component component);
    List<ErrorLog> getByEnvironment(Environment environment);


    
}
