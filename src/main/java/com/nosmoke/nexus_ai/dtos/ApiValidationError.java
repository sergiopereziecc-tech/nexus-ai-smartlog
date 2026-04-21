package com.nosmoke.nexus_ai.dtos;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiValidationError(
    LocalDateTime timestamp,
    String message,
    String path,
    Map<String, String> 
) {
    
}
