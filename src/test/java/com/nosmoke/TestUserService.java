package com.nosmoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nosmoke.nexus_ai.dtos.RegisterRequest;
import com.nosmoke.nexus_ai.model.User;
import com.nosmoke.nexus_ai.repository.UserRepository;
import com.nosmoke.nexus_ai.service.UserService;
import com.nosmoke.nexus_ai.utility.JwtUtil;

@ExtendWith(MockitoExtension.class)
public class TestUserService {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private  JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    RegisterRequest registerRequest = new RegisterRequest("user", "password", "hola@gmail.com");

    User user = new User();

    @Test
    void shouldRegister(){

        //Arrange

        when(passwordEncoder.encode(registerRequest.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        // when(jwtUtil.generateToken(any(User.class))).thenReturn("fakeToken");


        // String token = userService.register(registerRequest);
        // assertEquals("fakeToken", token);
        

        assertThrows(DataIntegrityViolationException.class, () -> userService.register(registerRequest));
          

    }

    @Test
    void shouldLoadUsername(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        assertEquals(user.getUsername(), userDetails.getUsername());
    }

     @Test
    void shouldNotLoadUsername(){
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        

        assertThrows(UsernameNotFoundException.class,() -> userService.loadUserByUsername(user.getUsername()));
    }
}
