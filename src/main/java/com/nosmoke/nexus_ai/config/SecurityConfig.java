package com.nosmoke.nexus_ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * SecurityConfig is the main Spring Security configuration class for the application.
 * It defines how the application handles authentication, authorization, and security policies.
 * 
 * Key responsibilities:
 * 1. Configures which endpoints are public vs protected
 * 2. Sets up the JWT filter to validate tokens on each request
 * 3. Disables session-based authentication (REST API uses stateless JWT)
 * 4. Provides beans for password encryption and authentication
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // The JWT filter that validates JWT tokens on each incoming request
    // It's injected here so we can add it to the security filter chain below
    private final JwtFilter jwtFilter;

    
    /**
     * Configures the main security filter chain for the application.
     * This bean defines the security rules for all HTTP requests.
     * 
     * @param httpSecurity The HttpSecurity object used to configure security
     * @return A configured SecurityFilterChain that Spring Security will use
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity
            // Disable CSRF protection
            // CSRF (Cross-Site Request Forgery) protection is typically needed for browser-based apps
            // that use cookies for authentication. Since this is a REST API using stateless JWT,
            // CSRF attacks aren't a concern, so we can disable it.
            .csrf(csrf -> csrf.disable())
            // Define authorization rules: which endpoints need authentication and which are public
            .authorizeHttpRequests(auth -> auth
                // Allow public access to all authentication endpoints (login, register, etc.)
                // These endpoints don't require a valid JWT token
                .requestMatchers("/api/auth/**").permitAll()
                // Require authentication for all other endpoints
                // Users must have a valid JWT token to access any other endpoint
                .anyRequest().authenticated()
            )
            // Configure session management
            // STATELESS means the server doesn't create or store user sessions
            // Instead, each request carries its own JWT token with user information
            // This is perfect for REST APIs and allows horizontal scaling (no session state to sync)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Register the JWT filter in the filter chain
            // It runs BEFORE the standard UsernamePasswordAuthenticationFilter
            // This means JWT validation happens first, extracting the user from the token
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
            
            
    }
        // Spring uses this to hash passwords before saving and to verify them on login
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
}
