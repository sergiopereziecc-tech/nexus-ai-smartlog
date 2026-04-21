package com.nosmoke.nexus_ai.dtos;

import java.time.LocalDateTime;

public record ApiError(

    //Record para errores genericos o de recursos no encontrados

        LocalDateTime timestamp,
        String message,
        String path) {

}
