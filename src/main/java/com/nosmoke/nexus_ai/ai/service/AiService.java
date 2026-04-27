package com.nosmoke.nexus_ai.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nosmoke.nexus_ai.ai.repository.AiRepository;
import com.nosmoke.nexus_ai.model.AiSolutions;
import com.nosmoke.nexus_ai.model.ErrorLog;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * AiService is a Spring service that leverages Google's Gemini AI API to analyze error logs
 * and provide intelligent solutions to developers.
 * 
 * Workflow:
 * 1. Receives an ErrorLog containing error message, stack trace, severity level, component, and environment
 * 2. Constructs a detailed prompt that provides context about the error
 * 3. Sends this prompt to Google's Gemini API for AI-powered analysis
 * 4. Parses the AI response to extract the solution
 * 5. Stores both the error log and solution in the database for future reference
 * 
 * This service acts as a bridge between the application's error handling system and AI capabilities.
 */
@Service
@RequiredArgsConstructor
@Component
public class AiService {

    // Repository for persisting AI solutions to the database
    // Allows us to store and retrieve error analysis results
    private final AiRepository aiRepository;
    
    // Spring's RestTemplate for making HTTP requests
    // We use this to communicate with the external Gemini API
    private final RestTemplate restTemplate;
    
    // Jackson's ObjectMapper for parsing JSON responses from the API
    // Helps us extract data from the complex JSON structure returned by Gemini
    private final ObjectMapper objectMapper;

    // The base URL for Google's Gemini API
    // Loaded from application configuration (application.properties or application.yml)
    @Value("${gemini.api.url}")
    private String apiUrl;

    // The API key for authenticating with Google's Gemini service
    // This is a sensitive credential and should be kept secure in production
    // Loaded from application configuration
    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Analyzes an error using Google's Gemini AI and returns an intelligent solution.
     * 
     * This method orchestrates the entire error analysis workflow:
     * 1. Constructs a detailed prompt from the error log information
     * 2. Sends it to the Gemini API
     * 3. Parses and extracts the AI-generated solution
     * 4. Stores the analysis result in the database
     * 
     * @param errorLog The error log containing message, stack trace, level, component, and environment
     * @return An AiSolutions object containing the AI-generated solution and associated error log
     * @throws JsonProcessingException if there's an error parsing the API response
     */
    public AiSolutions analyzeError(ErrorLog errorLog) throws JsonProcessingException {
        // Step 1: Build a comprehensive prompt for the Gemini API
        // This prompt provides all the context the AI needs to understand the problem:
        // - The error message (what went wrong)
        // - The stack trace (where and how it went wrong)
        // - The error level/severity (how critical it is)
        // - The component (which part of the system failed)
        // - The environment (production, staging, development - affects the solution approach)
        String prompt = "I want you to analyze this error using all the information below and give the user a solution based on his needs. \n"
                + "Use the error message to gather information about what the problem is " + errorLog.getErrorMessage()
                +
                "\n .Use the stack trace to have all the information of what is the context about the problem: "
                + errorLog.getStackTrace()
                + "\n. What kind of error it is based on the context: " + errorLog.getLevel()
                + "\n. Learn where the problem resides, analizy the error to see if it belongs in the backend, frontend or both, also the user might give you some info in that "
                + errorLog.getComponent()
                + "\n. Finally, based on the error log enviroment, if its prod, dev or staging. Base your solution around all the information: "
                + errorLog.getEnvironment();
        prompt = prompt.replace("\n", "\\n");

        // Step 2: Format the prompt into the JSON structure that Gemini API expects
        // Gemini API requires requests in a specific format with "contents" containing "parts" with "text"
        String requestBody = """
                {
                    "model": "llama-3.3-70b-versatile",
                    "messages": [
                    {"role": "user", "content": "%s"}
                    ]
                }
                """.formatted(prompt);

        // Step 3: Prepare HTTP headers for the API request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // Tell the API we're sending JSON
        headers.set("Authorization", "Bearer " + apiKey);

        // Step 4: Create an HTTP entity with the request body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Step 5: Send POST request to the Gemini API
        // Append the API key as a query parameter for authentication
        // The API returns a complex JSON response with the generated solution nested inside
        String responseBody = restTemplate.postForEntity(apiUrl, entity, String.class).getBody();

        // Step 6: Parse the complex JSON response to extract just the solution text
        // Gemini's response structure: response -> candidates[0] -> content -> parts[0] -> text
        // We navigate this tree to find the actual AI-generated solution
        String parsedResponse = objectMapper.readTree(responseBody)
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();

        // Step 7: Create an AiSolutions object to store the result
        AiSolutions aiSolutions = new AiSolutions();
        // Set the AI-generated solution text
        aiSolutions.setSolutionText(parsedResponse);
        // Link the solution to the original error log for reference and traceability
        aiSolutions.setErrorLog(errorLog);
        
        // Step 8: Persist the solution to the database
        // This allows users to see the solution later and helps build a knowledge base of solutions
        aiRepository.save(aiSolutions);
        
        // Step 9: Return the solution to the caller
        return aiSolutions;
    }

}
