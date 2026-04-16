package com.nosmoke.nexus_ai.service;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collector;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.mapper.ErrorMapper;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;
import com.nosmoke.nexus_ai.repository.ErrorLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService {

    private final ErrorLogRepository errorLogRepository;
    private final ErrorMapper errorMapper;

    @Override
    public ErrorResponse create(ErrorRequest errorRequest) {

        // El método create recibe un ErrorRequest, lo convierte a un ErrorLog
        // utilizando el errorMapper,
        // luego guarda el ErrorLog en la base de datos utilizando el
        // errorLogRepository,
        // y finalmente convierte el ErrorLog guardado de nuevo a un ErrorResponse para
        // devolverlo al cliente.
        ErrorLog errorLog = errorMapper.toEntity(errorRequest);
        ErrorLog savedErrorLog = errorLogRepository.save(errorLog);
        return errorMapper.toResponse(savedErrorLog);

    }

    @Override
    public ErrorResponse read(Long id) {
        // explicame que hace el map y el orElseThrow en este contexto
        // El método findById devuelve un Optional<ErrorLog>.
        // El método map se utiliza para transformar el ErrorLog en un ErrorResponse
        // utilizando el errorMapper.
        // Si el Optional contiene un valor, se aplicará la función de mapeo y se
        // devolverá un Optional<ErrorResponse>.
        // Si el Optional está vacío (es decir, no se encontró un ErrorLog con el ID
        // proporcionado ),
        // entonces el método orElseThrow se ejecutará, lanzando una RuntimeException
        // con el mensaje "Error log not found".

        return errorLogRepository.findById(id).map(errorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Error log not found"));
    }

    @Override
    public List<ErrorResponse> readAll() {
        // Find all devuelve una lista de todos los registros de ErrorLog en la base de
        // datos.
        return errorLogRepository.findAll().stream().map(errorMapper::toResponse)
        .collect(Collectors.toList());

    }

    @Override
    public void delete(Long id) {

        ErrorLog errorLog = errorLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error log not found"));
        errorLogRepository.delete(errorLog);

    }

    @Override
    public List<ErrorResponse> getByApplicationName(String applicationName) {
        return errorLogRepository.findByApplicationName(applicationName).stream()
                .map(errorMapper::toResponse).collect(Collectors.toList());

    }

    @Override
    public List<ErrorResponse> getByStatus(Status status) {
        return errorLogRepository.findByStatus(status).stream().map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ErrorResponse> getByLevel(Level level) {
        return errorLogRepository.findByLevel(level).stream().map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<ErrorResponse> getByComponent(Component component) {
        return errorLogRepository.findByComponent(component).stream().map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<ErrorResponse> getByEnvironment(Environment environment) {
        return errorLogRepository.findByEnvironment(environment).stream().map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }

}
