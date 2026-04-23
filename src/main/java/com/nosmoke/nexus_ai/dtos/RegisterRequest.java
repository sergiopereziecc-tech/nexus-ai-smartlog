package com.nosmoke.nexus_ai.dtos;

public record RegisterRequest(
    String username,
    String password,
    String email
) {
    
}
