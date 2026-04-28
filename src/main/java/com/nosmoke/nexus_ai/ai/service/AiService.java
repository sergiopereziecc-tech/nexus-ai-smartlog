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
 * AiService is a Spring service that leverages Groq's LLM API to analyze error logs
 * and provide intelligent solutions to developers.
 * 
 * Workflow:
 * 1. Receives an ErrorLog containing error message, stack trace, severity level, component, and environment
 * 2. Constructs a detailed prompt that provides context about the error
 * 3. Sends this prompt to Groq's API for AI-powered analysis
 * 4. Parses the AI response to extract the solution and confidence score
 * 5. Stores both the error log and solution in the database for future reference
 * 
 * This service acts as a bridge between the application's error handling system and AI capabilities.
 */
@Service
@RequiredArgsConstructor
public class AiService {

    // Repository for persisting AI solutions to the database
    // Allows us to store and retrieve error analysis results
    private final AiRepository aiRepository;

    // Spring's RestTemplate for making HTTP requests
    // We use this to communicate with the external Groq API
    private final RestTemplate restTemplate;

    // Jackson's ObjectMapper for parsing JSON responses from the API
    // Helps us extract data from the complex JSON structure returned by Groq
    private final ObjectMapper objectMapper;

    // The base URL for Groq's API
    // Loaded from application configuration (application.properties or application.yml)
    @Value("${gemini.api.url}")
    private String apiUrl;

    // The API key for authenticating with Groq's service
    // This is a sensitive credential and should be kept secure in production
    // Loaded from application configuration
    @Value("${gemini.api.key}")
    private String apiKey;

    /**
     * Analyzes an error using Groq's LLM API and returns an intelligent solution.
     * 
     * This method orchestrates the entire error analysis workflow:
     * 1. Constructs a detailed prompt from the error log information
     * 2. Sends it to the Groq API
     * 3. Parses and extracts the AI-generated solution and confidence score
     * 4. Stores the analysis result in the database
     * 
     * @param errorLog The error log containing message, stack trace, level, component, and environment
     * @return An AiSolutions object containing the AI-generated solution, confidence score, and associated error log
     * @throws JsonProcessingException if there's an error parsing the API response
     */
    public AiSolutions analyzeError(ErrorLog errorLog) throws JsonProcessingException {
        // Step 1: Build a comprehensive prompt for the Groq API
        // This prompt provides all the context the AI needs to understand the problem:
        // - The error message (what went wrong)
        // - The stack trace (where and how it went wrong)
        // - The error level/severity (how critical it is)
        // - The component (which part of the system failed)
        // - The environment (production, staging, development - affects the solution approach)
        // - Request for JSON-only response with solution and confidenceScore fields
        String prompt = "I want you to analyze this error as if you were a Senior Developer using all the information below and give the user a solution based on his needs. \n"
                + "Use the error message to gather information about what the problem is " + errorLog.getErrorMessage()
                +
                "\n .Use the stack trace to have all the information of what is the context about the problem: "
                + errorLog.getStackTrace()
                + "\n. What kind of error it is based on the context: " + errorLog.getLevel()
                + "\n. Learn where the problem resides, analizy the error to see if it belongs in the backend, frontend or both, also the user might give you some info in that "
                + errorLog.getComponent()
                + "\n. Finally, based on the error log enviroment, if its prod, dev or staging. Base your solution around all the information: "
                + errorLog.getEnvironment()
                + "Respond only in valid JSON format with exactly two fields: solution and confidenceScore (integer 0-100). No other text outside the JSON.";
        prompt = prompt.replace("\n", "\\n");

        // Step 2: Format the prompt into the JSON structure that Groq API expects
        // Groq API requires requests in a specific format with "model", "messages" array
        // The messages array contains objects with "role" (user/assistant) and "content"
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
        headers.set("Authorization", "Bearer " + apiKey); // Add Bearer token for authentication

        // Step 4: Create an HTTP entity with the request body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Step 5: Send POST request to the Groq API
        // The API returns a JSON response with the generated solution nested inside a specific structure
        String responseBody = restTemplate.postForEntity(apiUrl, entity, String.class).getBody();

        // Step 6: Parse the complex JSON response to extract the solution and confidence score
        // Groq's response structure: response -> choices[0] -> message -> content
        // The content is a JSON string containing the actual solution and confidenceScore
        // We navigate this tree to find the raw response, then parse it as JSON again
        String parsedResponse = objectMapper.readTree(responseBody)
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();
        // Parse the JSON content to extract the solution
        String parsedSolution = objectMapper.readTree(parsedResponse)
                .get("solution")
                .asText();
        // Parse the JSON content to extract the confidence score (0-100)
        Integer parsedConfidenceScore = objectMapper.readTree(parsedResponse)
                .get("confidenceScore")
                .asInt();

        // Step 7: Create an AiSolutions object to store the result
        AiSolutions aiSolutions = new AiSolutions();
        // Set the AI-generated solution text
        aiSolutions.setSolutionText(parsedSolution);
        // Set the confidence score (0-100) indicating how confident the AI is in the solution
        aiSolutions.setConfidenceScore(parsedConfidenceScore);
        // Link the solution to the original error log for reference and traceability
        aiSolutions.setErrorLog(errorLog);

        // Step 8: Persist the solution to the database
        // This allows users to see the solution later and helps build a knowledge base
        // of solutions
        aiRepository.save(aiSolutions);

        // Step 9: Return the solution to the caller
        return aiSolutions;
    }

}
