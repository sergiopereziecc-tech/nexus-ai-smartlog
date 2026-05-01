package com.nosmoke.nexus_ai.service;

import java.util.List;
import java.util.Optional;

import java.util.stream.Collector;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nosmoke.nexus_ai.ai.service.AiService;
import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.exception.ResourceNotFound;
import com.nosmoke.nexus_ai.mapper.ErrorMapper;
import com.nosmoke.nexus_ai.model.AiSolutions;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;
import com.nosmoke.nexus_ai.repository.ErrorLogRepository;


import lombok.RequiredArgsConstructor;

/**
 * ErrorServiceImpl is a service class that handles all business logic for error log management.
 * 
 * This service is responsible for:
 * 1. Converting between Data Transfer Objects (DTOs) and database entities using mappers
 * 2. Persisting error logs to the database through the repository
 * 3. Triggering AI analysis on newly created error logs
 * 4. Filtering and retrieving error logs by various criteria
 * 
 * The service acts as a middle layer between the controller (API endpoints) and the database.
 * It orchestrates interactions between the repository (database access) and the AI service (error analysis).
 */
@Service
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService {

    // Repository for database access to ErrorLog entities
    // Provides methods like save(), findById(), findAll(), findByStatus(), etc.
    private final ErrorLogRepository errorLogRepository;
    
    // Mapper for converting between ErrorRequest/ErrorResponse DTOs and ErrorLog entities
    // DTOs are lightweight objects used for API communication
    // Entities are the database representations
    private final ErrorMapper errorMapper;
    
    // AI service for analyzing error logs
    // When a new error is created, it's automatically sent to this service
    // for AI-powered analysis and solution generation
    private final AiService aiService;

    /**
     * Creates a new error log and triggers AI analysis.
     * 
     * This method orchestrates the complete error creation workflow:
     * 1. Converts the incoming ErrorRequest DTO to an ErrorLog entity
     * 2. Persists the error log to the database
     * 3. Sends the error log to the AI service for automated analysis
     * 4. Retrieves the saved error log and converts it back to a response DTO
     * 
     * Flow:
     * - Receive ErrorRequest from API client
     * - Map to ErrorLog entity
     * - Save to database
     * - Trigger AI analysis asynchronously
     * - Return the saved error log as an ErrorResponse
     * 
     * @param errorRequest The error information from the client
     * @return ErrorResponse containing the newly created error log details
     * @throws JsonProcessingException if there's an error processing JSON during AI analysis
     */
    @Override
   
    public ErrorResponse create(ErrorRequest errorRequest) throws JsonProcessingException{
        // Step 1: Convert the incoming DTO to a database entity
        // The mapper transforms the client request into the entity format
        ErrorLog errorLog = errorMapper.toEntity(errorRequest);
        
        // Step 2: Save the error log to the database
        // This assigns an ID and persists the record
        ErrorLog savedErrorLog = errorLogRepository.save(errorLog);
        
        // Step 3: Send the saved error log to the AI service for analysis
        // This triggers AI-powered analysis to generate a solution
        // The AI will examine the error message, stack trace, component, etc.
        AiSolutions solution = aiService.analyzeError(savedErrorLog);
        savedErrorLog.getAiSolutions().add(solution);
        // Step 4: Retrieve the saved error log and convert back to a response
        // We query the database again to ensure we have the latest data
        // Then map it to an ErrorResponse to send back to the client
        return errorMapper.toResponse(savedErrorLog);
    }

    /**
     * Retrieves a single error log by its ID.
     * 
     * This method demonstrates the use of Optional for safe null handling.
     * Instead of returning null when not found, we use Optional and chain operations.
     * 
     * Flow:
     * 1. Query database by ID, which returns an Optional<ErrorLog>
     * 2. If present, map the ErrorLog to an ErrorResponse using the mapper
     * 3. If not present, throw a ResourceNotFound exception
     * 
     * @param id The unique identifier of the error log to retrieve
     * @return ErrorResponse containing the error log details
     * @throws ResourceNotFound if no error log with this ID exists
     */
    @Override
    public ErrorResponse read(Long id) {
        // Query the database for an error log by ID
        // Optional.findById() returns Optional<ErrorLog> which is either:
        // - Optional.of(errorLog) if found
        // - Optional.empty() if not found
        return errorLogRepository.findById(id)
                // Step 1: If the error log exists, map it to a response
                // The map() method applies the mapper only if a value is present
                .map(errorMapper::toResponse)
                // Step 2: If the Optional is empty (error log not found), throw exception
                // orElseThrow() throws if no value is present
                .orElseThrow(() -> new ResourceNotFound("Error log with ID: " + id + " not found"));
    }

    /**
     * Retrieves all error logs from the database.
     * 
     * This method demonstrates using Java Streams for functional transformation.
     * - findAll() returns a List<ErrorLog> from the database
     * - stream() converts the list into a Stream for processing
     * - map() transforms each ErrorLog to an ErrorResponse
     * - collect(Collectors.toList()) gathers results back into a List
     * 
     * Benefits of this approach:
     * - Functional style is more readable and declarative
     * - Easy to extend (e.g., add filters, sorting)
     * - Efficient for larger datasets
     * 
     * @return List of all ErrorResponse objects in the system
     */
    @Override
    public List<ErrorResponse> readAll() {
        // Retrieve all error logs from database and convert to responses
        return errorLogRepository.findAll()
                // Convert the list to a stream for processing
                .stream()
                // Transform each ErrorLog entity to an ErrorResponse DTO
                .map(errorMapper::toResponse)
                // Collect all results back into a list
                .collect(Collectors.toList());
    }

    /**
     * Deletes an error log by its ID.
     * 
     * This method ensures safe deletion by:
     * 1. First checking if the error log exists
     * 2. Only deleting if found
     * 3. Throwing an exception if not found (prevents accidental silences)
     * 
     * This prevents the common mistake of silently failing when trying to delete
     * a non-existent record, which could hide bugs in the application.
     * 
     * @param id The unique identifier of the error log to delete
     * @throws ResourceNotFound if no error log with this ID exists
     */
    @Override
    public void delete(Long id) {
        // Step 1: Attempt to find the error log by ID
        ErrorLog errorLog = errorLogRepository.findById(id)
                // Step 2: If not found, throw an exception
                // This fails fast and alerts us to the problem
                .orElseThrow(() -> new ResourceNotFound("Error log with ID: " + id + " not found"));
        
        // Step 3: Delete the found error log from the database
        errorLogRepository.delete(errorLog);
    }

    /**
     * Retrieves all error logs matching a specific application name.
     * 
     * This method demonstrates custom database queries for filtering.
     * The repository provides domain-specific methods beyond standard CRUD operations.
     * 
     * @param applicationName The name of the application to filter by
     * @return List of ErrorResponse objects for the specified application
     */
    @Override
    public List<ErrorResponse> getByApplicationName(String applicationName) {
        // Query database for all errors from a specific application
        return errorLogRepository.findByApplicationName(applicationName)
                // Convert stream and map each entity to a response
                .stream()
                .map(errorMapper::toResponse)
                // toList() is a newer Java Stream method (Java 16+)
                .toList();
    }

    /**
     * Retrieves all error logs matching a specific status.
     * 
     * Status values represent the progress of error resolution:
     * - PENDING: Error reported but not yet analyzed
     * - IN_PROGRESS: Being worked on
     * - RESOLVED: Solution applied successfully
     * 
     * @param status The Status enum value to filter by
     * @return List of ErrorResponse objects with the specified status
     */
    @Override
    public List<ErrorResponse> getByStatus(Status status) {
        // Query database for errors with a specific resolution status
        return errorLogRepository.findByStatus(status)
                // Convert and map to response DTOs
                .stream()
                .map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all error logs matching a specific severity level.
     * 
     * Severity levels:
     * - INFO: Informational messages
     * - WARN: Warnings that should be investigated
     * - ERROR: Critical errors requiring immediate attention
     * - DEBUG: Debugging information
     * 
     * @param level The Level enum value to filter by
     * @return List of ErrorResponse objects with the specified severity level
     */
    @Override
    public List<ErrorResponse> getByLevel(Level level) {
        // Query database for errors with a specific severity level
        return errorLogRepository.findByLevel(level)
                .stream()
                .map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves all error logs matching a specific component.
     * 
     * Components identify which part of the system experienced the error:
     * - BACKEND: Server-side application code
     * - FRONTEND: Client-side/browser code
     * - DATABASE: Database layer issues
     * - INFRASTRUCTURE: Network, deployment, or infrastructure issues
     * 
     * @param component The Component enum value to filter by
     * @return List of ErrorResponse objects from the specified component
     */
    @Override
    public List<ErrorResponse> getByComponent(Component component) {
        // Query database for errors from a specific system component
        return errorLogRepository.findByComponent(component)
                .stream()
                .map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }
    /**
     * Retrieves all error logs matching a specific environment.
     * 
     * Environments:
     * - DEV: Development environment (local testing)
     * - STAGING: Pre-production environment (testing before release)
     * - PROD: Production environment (live system, user-facing)
     * 
     * Errors from different environments may have different priorities and handling procedures.
     * Production errors typically require immediate attention.
     * 
     * @param environment The Environment enum value to filter by
     * @return List of ErrorResponse objects from the specified environment
     */
    @Override
    public List<ErrorResponse> getByEnvironment(Environment environment) {
        // Query database for errors from a specific deployment environment
        return errorLogRepository.findByEnvironment(environment)
                .stream()
                .map(errorMapper::toResponse)
                .collect(Collectors.toList());
    }
}
