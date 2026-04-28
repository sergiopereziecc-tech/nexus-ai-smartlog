package com.nosmoke.nexus_ai.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.nosmoke.nexus_ai.dtos.LoginRequest;
import com.nosmoke.nexus_ai.dtos.RegisterRequest;
import com.nosmoke.nexus_ai.model.User;
import com.nosmoke.nexus_ai.repository.UserRepository;
import com.nosmoke.nexus_ai.utility.JwtUtil;

import lombok.RequiredArgsConstructor;

/**
 * UserService is a core service that handles user management and authentication.
 * 
 * It implements Spring Security's UserDetailsService interface, which is required for
 * integrating with Spring Security's authentication system.
 * 
 * Responsibilities:
 * 1. Register new users with encrypted passwords
 * 2. Load user information from the database by username
 * 3. Provide user details to Spring Security for authentication and authorization
 * 
 * This service acts as a bridge between the database (via UserRepository) and Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    // Repository for accessing user data from the database
    // Provides methods like findByUsername() to retrieve user records
    private final UserRepository userRepository;

    // PasswordEncoder from Spring Security for hashing passwords
    // Uses BCrypt to securely encrypt passwords before storing in the database
    // Also used to verify passwords during login by comparing hashes
    private final PasswordEncoder passwordEncoder;

    // JwtUtil for generating JWT tokens
    // Used during registration to provide an immediate token without requiring login
    private final JwtUtil jwtUtil;

    

    /**
     * Loads user information by username from the database.
     * 
     * This method is part of the UserDetailsService interface required by Spring Security.
     * Spring Security calls this method when:
     * - A user attempts to login (AuthenticationManager uses it to load user credentials)
     * - The JwtFilter needs to load user details to validate a JWT token
     * 
     * Flow:
     * 1. Queries the UserRepository for a user with the given username
     * 2. If found, returns the User object (which implements UserDetails)
     * 3. If not found, throws UsernameNotFoundException
     * 
     * @param username The username to search for
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if no user with that username exists in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
    }

    /**
     * Registers a new user in the system.
     * 
     * This method creates a new user account with encrypted password and
     * returns a JWT token for immediate authentication.
     * 
     * Flow:
     * 1. Create a new User object
     * 2. Encrypt the provided password using BCryptPasswordEncoder
     *    - BCrypt adds a random salt and hashes the password
     *    - This means the same password produces different hashes each time
     *    - The hash is one-way: cannot decrypt to get the original password
     * 3. Set the username and email from the registration request
     * 4. Assign the default USER role (not admin)
     * 5. Save the new user to the database
     * 6. Generate a JWT token so the user is immediately authenticated
     * 7. Return the token to the client
     * 
     * Security considerations:
     * - Password is hashed before storage, so plaintext password is never saved
     * - Each user gets a default USER role (can be changed by admins)
     * - The returned JWT token allows the user to make authenticated requests
     * 
     * @param registerRequest Contains username, email, and password for the new user
     * @return A JWT token that the newly registered user can use for authenticated requests
     */
    public String register(RegisterRequest registerRequest) {
        // Step 1: Create a new User entity object
        User user = new User();

        // Step 2: Encrypt the password before storing it
        // The passwordEncoder.encode() method applies BCrypt hashing with a random salt
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        
        // Step 3: Set the username and email from the registration request
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        
        // Step 4: Assign the default USER role
        // This role grants standard user permissions (not admin privileges)
        user.setRole(User.Role.USER);

        // Step 5: Save the new user to the database
        userRepository.save(user);

        // Step 6: Generate and return a JWT token
        // This allows the newly registered user to immediately make authenticated requests
        // without needing to log in again
        return jwtUtil.generateToken(user);
    }
    

}
