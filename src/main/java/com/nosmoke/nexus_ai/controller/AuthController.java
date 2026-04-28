package com.nosmoke.nexus_ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nosmoke.nexus_ai.dtos.LoginRequest;
import com.nosmoke.nexus_ai.dtos.RegisterRequest;
import com.nosmoke.nexus_ai.service.UserService;
import com.nosmoke.nexus_ai.utility.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * AuthController is the REST API endpoint handler for all authentication operations.
 * 
 * This controller handles two critical workflows:
 * 1. User Registration - Creates new user accounts and returns a JWT token
 * 2. User Login - Authenticates existing users and returns a JWT token
 * 
 * All endpoints in this controller are public (permitting anonymous access) as defined in SecurityConfig.
 * This is necessary because users need to register/login before they can have a valid JWT token.
 * 
 * Request Path: /api/auth
 * - POST /api/auth/register - Register a new user
 * - POST /api/auth/login - Authenticate and get a JWT token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    // UserService handles user registration and loading user details from the database
    private final UserService userService;
    
    // AuthenticationManager from Spring Security authenticates user credentials (username + password)
    // It verifies the user exists and the password is correct
    private final AuthenticationManager authenticationManager;
    
    // JwtUtil generates JWT tokens for authenticated users
    private final JwtUtil jwtUtil;

    /**
     * Endpoint for user registration.
     * 
     * This endpoint creates a new user account in the system and immediately returns a JWT token.
     * 
     * Flow:
     * 1. Receives a RegisterRequest with username, email, and password
     * 2. Passes the request to UserService for registration
     * 3. UserService creates the user with encrypted password and default USER role
     * 4. Returns a JWT token so the user is immediately authenticated
     * 
     * HTTP Status:
     * - 201 CREATED: Successfully created a new user account
     * 
     * Security:
     * - Public endpoint (no authentication required)
     * - Password is encrypted before being stored in the database
     * 
     * @param request RegisterRequest containing username, email, and password
     * @return ResponseEntity with HTTP 201 status and a JWT token as the body
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // Call the UserService to handle all registration logic
        // Returns a JWT token that the user can use for subsequent authenticated requests
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    /**
     * Endpoint for user login (authentication).
     * 
     * This endpoint authenticates an existing user by verifying their credentials
     * and returns a JWT token for subsequent authenticated requests.
     * 
     * Flow:
     * 1. Receives a LoginRequest with username and password
     * 2. Uses AuthenticationManager to verify the credentials
     *    - Looks up the user by username
     *    - Hashes the provided password and compares it with the stored hash
     *    - Throws exception if credentials are invalid
     * 3. If authentication succeeds, loads the full UserDetails object
     * 4. Generates a JWT token containing the user information
     * 5. Returns the token to the client
     * 
     * HTTP Status:
     * - 202 ACCEPTED: Authentication was successful
     * - 401 Unauthorized: If credentials are invalid (thrown by Spring Security)
     * 
     * Security:
     * - Public endpoint (no authentication required to attempt login)
     * - Passwords are hashed and compared using BCrypt
     * - Invalid credentials trigger Spring Security's exception handling
     * 
     * @param request LoginRequest containing username and password
     * @return ResponseEntity with HTTP 202 status and a JWT token as the body
     * @throws AuthenticationException if the credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // Step 1: Authenticate the user credentials
        // This verifies that the username exists and the password is correct
        // UsernamePasswordAuthenticationToken holds the unauthenticated credentials
        // The AuthenticationManager will validate them or throw an exception
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        
        // Step 2: Load the complete UserDetails from the database
        // This retrieves all user information (username, authorities, enabled status, etc.)
        UserDetails userDetails = userService.loadUserByUsername(request.username());

        // Step 3: Generate and return a JWT token
        // The token contains the user information and is signed with the secret key
        // The client will send this token in the Authorization header for subsequent requests
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jwtUtil.generateToken(userDetails));
    }
    
    
    
}
