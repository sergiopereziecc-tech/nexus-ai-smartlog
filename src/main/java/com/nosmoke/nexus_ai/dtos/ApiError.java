package com.nosmoke.nexus_ai.dtos;

import java.time.LocalDateTime;

public record ApiError(
    LocalDateTime timestamp,
    String message,
    String path
) {
    
}
