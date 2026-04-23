package com.nosmoke.nexus_ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
