package com.nosmoke.nexus_ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.nosmoke.nexus_ai.service.ErrorServiceImpl;

/**
 * TestErrorServiceImpl is a unit test class for the ErrorServiceImpl service.
 * 
 * Unit testing is critical for ensuring code quality and catching bugs early.
 * This class uses Mockito to isolate the ErrorServiceImpl from its dependencies.
 * 
 * Key concepts:
 * - @ExtendWith(MockitoExtension.class): Enables Mockito annotations in this test class
 * - @Mock: Creates mock objects that simulate dependencies
 * - @InjectMocks: Injects mock dependencies into the class being tested
 * - Mocks allow us to control external behavior and verify interactions
 * 
 * Testing approach: AAA Pattern (Arrange, Act, Assert)
 * - Arrange: Set up test data and mock behavior
 * - Act: Call the method being tested
 * - Assert: Verify the results are correct
 * 
 * Benefits of this approach:
 * - Tests only the ErrorServiceImpl logic, not database or mapper behavior
 * - Tests run fast (no database access)
 * - Tests are reliable (no external dependencies)
 * - Bugs are isolated to the tested component
 */
@ExtendWith(MockitoExtension.class)
public class TestErrorServiceImpl {
    
    // Mock object for ErrorMapper
    // Mocking means we create a fake version that we can control in tests
    // This allows us to test ErrorServiceImpl without depending on the real mapper logic
    @Mock
    private ErrorMapper errorMapper;

    // Mock object for ErrorLogRepository
    // We mock the database repository so tests don't need a real database
    // We can tell it to return specific values for specific queries
    @Mock
    private ErrorLogRepository errorLogRepository;

    // The class being tested, with mocks injected
    // @InjectMocks automatically injects the @Mock fields into ErrorServiceImpl
    // So ErrorServiceImpl uses our mock repository and mapper, not the real ones
    @InjectMocks
    private ErrorServiceImpl errorServiceImpl;

    // Test data: A sample error request from the client
    // This simulates what a user would send when reporting an error
    ErrorRequest errorRequest = new ErrorRequest("Poster", "Error", "StackTrace", ErrorLog.Environment.DEV, 
        ErrorLog.Component.BACKEND, ErrorLog.Level.INFO);

    // Test data: An error log as it would be stored in the database
    // This is what the mapper should convert the ErrorRequest into
    ErrorLog errorLog = new ErrorLog(1L, null, "Poster", "Error",
     "StackTrace", null, null, null,
      Environment.DEV, Level.INFO, Status.PENDING, Component.BACKEND,List.of());
    
    // Test data: An error response as it would be returned to the client
    // This is what the mapper should convert the ErrorLog into
    ErrorResponse errorResponse = new ErrorResponse(1L, null, "Poster", Environment.DEV, 
        Status.PENDING, Level.INFO, Component.BACKEND, null, null);
    

    /**
     * Test: Should successfully create an error log.
     * 
     * This test verifies that the create() method correctly:
     * 1. Converts the ErrorRequest to an ErrorLog entity using the mapper
     * 2. Saves the ErrorLog to the repository
     * 3. Converts the saved ErrorLog back to an ErrorResponse using the mapper
     * 4. Returns the correct response to the client
     * 
     * AAA Pattern:
     * - Arrange: Set up mock behavior for mapper and repository
     * - Act: Call the create() method with test data
     * - Assert: Verify the returned response matches the expected response
     */
    @Test
    void shouldCreateErrorLog() throws JsonProcessingException{
        // Arrange: Configure the mocks to return specific values
        // Tell the mapper to convert errorRequest -> errorLog
        when(errorMapper.toEntity(errorRequest)).thenReturn(errorLog);
        // Tell the repository to save and return the errorLog
        when(errorLogRepository.save(errorLog)).thenReturn(errorLog);
        // Tell the mapper to convert errorLog -> errorResponse
        when(errorMapper.toResponse(errorLog)).thenReturn(errorResponse);

        // Act: Call the method being tested
        ErrorResponse errorResponseTest = errorServiceImpl.create(errorRequest);

        // Assert: Verify the result is what we expected
        assertEquals(errorResponseTest, errorResponse);
    }

    /**
     * Test: Should find and return an error log by ID.
     * 
     * This test verifies that the read() method correctly:
     * 1. Queries the repository for an error log by ID
     * 2. Handles the case where the error log exists
     * 3. Converts the found ErrorLog to an ErrorResponse
     * 4. Returns the correct response
     * 
     * AAA Pattern:
     * - Arrange: Mock the repository to return an existing error log
     * - Act: Call read() with a valid ID
     * - Assert: Verify the returned response is correct
     */
    @Test
    void shouldFindErrorLogById(){
        // Arrange: Set up mocks to simulate finding an error log
        // Tell the repository to return an Optional with our test error log
        when(errorLogRepository.findById(errorLog.getId())).thenReturn(Optional.of(errorLog));
        // Tell the mapper to convert the found error log to a response
        when(errorMapper.toResponse(errorLog)).thenReturn(errorResponse);

        // Act: Call the read() method with the error log ID
        ErrorResponse errorResponseTest = errorServiceImpl.read(errorLog.getId());

        // Assert: Verify the returned response matches what we expected
        assertEquals(errorResponseTest, errorResponse);
    }

    /**
     * Test: Should throw an exception when error log is not found.
     * 
     * This test verifies error handling when:
     * 1. User tries to read an error log by ID
     * 2. The ID doesn't exist in the database
     * 3. The service correctly throws a ResourceNotFound exception
     * 
     * Testing error cases is as important as testing success cases.
     * 
     * AAA Pattern:
     * - Arrange: Mock the repository to return empty (not found)
     * - Act: Call read() with a non-existent ID
     * - Assert: Verify that ResourceNotFound exception is thrown
     */
    @Test
    void shouldNotFindErrorLogById(){
        // Arrange: Set up mock to simulate ID not found in database
        when(errorLogRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that calling read() with invalid ID throws ResourceNotFound
        // assertThrows checks that the exception is thrown
        assertThrows(ResourceNotFound.class, () -> errorServiceImpl.read(5L));
    }

    /**
     * Test: Should successfully delete an error log when ID is found.
     * 
     * This test verifies that the delete() method:
     * 1. Finds the error log by ID
     * 2. Calls the repository's delete method with the correct error log
     * 3. Properly cleans up the database record
     * 
     * Note: We use verify() to check that delete() was actually called
     * This is different from assertEquals() - we're verifying a method was invoked.
     * 
     * AAA Pattern:
     * - Arrange: Mock the repository to return an existing error log
     * - Act: Call delete() with a valid ID
     * - Assert: Verify that repository.delete() was called with the correct object
     */
    @Test
    void deleteIfIdWasFound(){
        // Arrange: Set up mock to return our test error log
        when(errorLogRepository.findById(errorLog.getId())).thenReturn(Optional.of(errorLog));
        
        // Act: Call the delete() method
        errorServiceImpl.delete(errorLog.getId());

        // Assert: Verify that delete() was called on the repository with our error log
        // verify() checks that the method was actually invoked
        verify(errorLogRepository).delete(errorLog);
    }

    /**
     * Test: Should throw an exception when trying to delete a non-existent error log.
     * 
     * This test verifies that delete() handles invalid IDs correctly:
     * 1. User tries to delete an error log by ID
     * 2. The ID doesn't exist in the database
     * 3. The service correctly throws a ResourceNotFound exception
     * 
     * AAA Pattern:
     * - Arrange: Mock the repository to return empty (not found)
     * - Act: Call delete() with a non-existent ID
     * - Assert: Verify that ResourceNotFound exception is thrown
     */
    @Test
    void deleteIfIdNotFound(){
        // Arrange: Set up mock to simulate ID not found
        when(errorLogRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert: Verify that delete() throws ResourceNotFound for non-existent ID
        assertThrows(ResourceNotFound.class, () -> errorServiceImpl.delete(5L));
    }

    /**
     * Test: Should find all error logs by application name.
     * 
     * This test verifies that the getByApplicationName() method:
     * 1. Queries the repository for all error logs matching an application name
     * 2. Handles lists of error logs (multiple results)
     * 3. Converts all ErrorLog objects to ErrorResponse objects using the mapper
     * 4. Returns the correct list of responses
     * 
     * This is a more complex test that handles collections instead of single objects.
     * 
     * AAA Pattern:
     * - Arrange: Set up mocks with lists of test data
     * - Act: Call getByApplicationName() with an application name
     * - Assert: Verify the returned list matches the expected list
     */
    @Test
    void shouldFindByApplicationName(){
        // Arrange: Create lists of test data
        // MockEntities represents the error logs from the database
        List<ErrorLog> mockEntities = List.of(errorLog);
        // ExpectedList represents what the client should receive
        List<ErrorResponse> expectedList = List.of(errorResponse);
        
        // Tell the repository to return our mock list when searching by application name
        when(errorLogRepository.findByApplicationName(errorLog.getApplicationName())).thenReturn(mockEntities);
        // Tell the mapper to convert each error log to a response
        when(errorMapper.toResponse(errorLog)).thenReturn(errorResponse);

        // Act: Call getByApplicationName() to search for error logs
        List<ErrorResponse> result = errorServiceImpl.getByApplicationName(errorLog.getApplicationName());

        // Assert: Verify the returned list matches what we expected
        assertEquals(expectedList, result);
    }
}
