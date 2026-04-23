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


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        
        UserDetails userDetails = userService.loadUserByUsername(request.username());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jwtUtil.generateToken(userDetails));
        
    }
    
    
    
}
