package com.nosmoke.nexus_ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * BeanConfig is a Spring configuration class that defines reusable beans for the entire application.
 * 
 * @Configuration tells Spring that this class contains bean definitions.
 * Beans are objects managed by Spring that can be injected into other classes.
 * 
 * This class centralizes the creation of utility objects needed across the application:
 * - Password encryption for user authentication
 * - Authentication management for login processes
 * - HTTP client for making external API calls
 * - JSON serialization/deserialization for API communication
 * 
 * By defining these as beans here, they can be injected anywhere in the application
 * via @Autowired or constructor injection, avoiding code duplication.
 */
@Configuration
public class BeanConfig {

    /**
     * Creates and provides a PasswordEncoder bean for the application.
     * 
     * The PasswordEncoder is used in two scenarios:
     * 1. When registering a new user: the plain-text password is hashed before being stored in the database
     * 2. When logging in: the user's submitted password is hashed and compared against the stored hash
     * 
     * BCryptPasswordEncoder uses bcrypt, a robust hashing algorithm that:
     * - Is slow by design (makes brute force attacks impractical)
     * - Includes a salt (random value) to prevent rainbow table attacks
     * - Can be made slower over time as computing power increases
     * 
     * @return A BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and provides an AuthenticationManager bean for the application.
     * 
     * The AuthenticationManager is responsible for authenticating users during login.
     * It handles the process of:
     * 1. Looking up the user in the database by username
     * 2. Hashing the submitted password
     * 3. Comparing it against the stored password hash
     * 4. Returning authentication success or failure
     * 
     * This bean is typically used by login endpoints to authenticate user credentials
     * and generate JWT tokens for successful logins.
     * 
     * @param config The AuthenticationConfiguration object that holds the authentication manager
     * @return An AuthenticationManager instance
     * @throws Exception if there's an error retrieving the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    /**
     * Creates and provides a RestTemplate bean for the application.
     * 
     * RestTemplate is Spring's client for making synchronous HTTP requests to external APIs.
     * 
     * Used for:
     * - Making GET, POST, PUT, DELETE requests to external services
     * - In this application, it's used to communicate with Google's Gemini AI API
     * - Sending error logs for analysis and receiving AI-generated solutions
     * 
     * Key features:
     * - Handles HTTP connection management automatically
     * - Supports different HTTP methods and content types
     * - Automatically converts request/response bodies to/from JSON or XML
     * 
     * By creating it as a bean, Spring manages its lifecycle and we can reuse the same
     * instance across the application, which is more efficient than creating new instances.
     * 
     * @return A RestTemplate instance ready for making HTTP requests
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    /**
     * Creates and provides an ObjectMapper bean for the application.
     * 
     * ObjectMapper is from the Jackson library and handles JSON serialization/deserialization.
     * 
     * Used for:
     * - Converting Java objects to JSON strings (serialization)
     * - Converting JSON strings to Java objects (deserialization)
     * - In this application, it's used to parse API responses from Google's Gemini AI
     * - Also handles navigation through complex nested JSON structures
     * 
     * Key features:
     * - Automatically maps JSON fields to Java object properties
     * - Supports complex types, nested objects, and collections
     * - Can be configured for custom serialization/deserialization behavior
     * - Thread-safe and reusable
     * 
     * By creating it as a bean, the same ObjectMapper instance is reused throughout
     * the application, which improves performance and ensures consistent configuration.
     * 
     * @return An ObjectMapper instance ready for JSON processing
     */
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    
}
