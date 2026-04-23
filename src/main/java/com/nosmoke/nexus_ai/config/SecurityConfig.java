package com.nosmoke.nexus_ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
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
        
    
    
}
