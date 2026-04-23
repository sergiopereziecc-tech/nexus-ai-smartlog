package com.nosmoke.nexus_ai.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nosmoke.nexus_ai.utility.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JwtFilter is a Spring Security filter that intercepts HTTP requests to validate JWT tokens.
 * 
 * How it works:
 * 1. For each incoming HTTP request, this filter checks the Authorization header
 * 2. If a valid JWT token is found, it extracts the user information from it
 * 3. It validates the token to ensure it's legitimate and not expired
 * 4. If valid, it creates a Spring Security Authentication object and sets it in the security context
 * 5. This allows the request to proceed through the application as an authenticated user
 * 
 * OncePerRequestFilter ensures this filter runs exactly once per request, even with request forwarding.
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // JwtUtil is injected here and provides methods to extract and validate JWT tokens
    private final JwtUtil jwtUtil;

    // UserDetailsService is Spring Security's interface for loading user information from a database or other source
    // We use it to load the full UserDetails object once we know the username from the JWT token
    private final UserDetailsService userDetailsService;

    /**
     * This method is called for every HTTP request that passes through this filter.
     * It's the core logic that handles JWT authentication.
     * 
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The chain of other filters to call after this one
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        // This header contains the JWT token sent by the client
        // Expected format: "Bearer eyJhbGciOiJIUzI1NiIs..."
        String header = request.getHeader("Authorization");

        // Step 2: Check if Authorization header exists and starts with "Bearer "
        // "Bearer" is the standard prefix for JWT tokens in HTTP headers
        // If the header is missing or doesn't start with "Bearer ", we skip JWT validation
        if (header == null || !header.startsWith("Bearer ")) {
            // No JWT token found, pass the request to the next filter in the chain
            // The user will be unauthenticated, but they may be able to access public endpoints
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token from the header
        // Remove the "Bearer " prefix (which is 7 characters long) to get just the token
        String token = header.substring(7);

        // Step 4: Extract the username from the JWT token
        // The token contains encoded user information - we decode it to get the username
        String username = jwtUtil.extractUsername(token);

        // Step 5: Load the complete user details from the database/service
        // We need the full UserDetails object to validate permissions and create the authentication token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Step 6: Validate the JWT token
        // Check if the token hasn't expired and still belongs to this user
        if (jwtUtil.validateToken(token, userDetails)) {
            // Token is valid! Create a Spring Security authentication token
            // This represents the authenticated user in Spring Security
            // "authenticated" status means the user has already been verified
            UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken.authenticated(username,
                    userDetails, userDetails.getAuthorities());
            
            // Step 7: Add request details to the authentication token
            // This includes information like IP address and session ID
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Step 8: Store the authentication in Spring Security's context
            // This makes the user "logged in" for the duration of this request
            // Spring Security and other components can now check who the current user is
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        
        // Step 9: Pass the request to the next filter in the chain
        // Whether or not the token was valid, we let the request continue
        // Invalid tokens just mean the user stays unauthenticated
        filterChain.doFilter(request, response);
    }
}
