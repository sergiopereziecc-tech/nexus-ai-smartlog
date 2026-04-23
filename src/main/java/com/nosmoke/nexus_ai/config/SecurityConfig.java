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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        return httpSecurity
            // Disable CSRF — not needed for REST APIs using JWT (no browser sessions/cookies)
            .csrf(csrf -> csrf.disable())
            // Define which endpoints are public and which require authentication
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // login and register are public
                .anyRequest().authenticated()               // everything else requires a token
            )
            // Stateless — no sessions stored on server, every request uses JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .build();
            
            
    }
        // Spring uses this to hash passwords before saving and to verify them on login
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); 
            
        
    }
    // Spring uses this to perform authentication (check username + password)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
