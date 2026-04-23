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

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));

    }

    public String register(RegisterRequest registerRequest) {
        User user = new User();

        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setRole(User.Role.USER);

        userRepository.save(user);

        return jwtUtil.generateToken(user);

    }
    

}
