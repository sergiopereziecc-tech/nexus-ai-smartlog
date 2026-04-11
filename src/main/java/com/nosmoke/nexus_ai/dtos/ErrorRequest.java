package com.nosmoke.nexus_ai.dtos;

import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;

public record ErrorRequest(String applicationName, String errorMessage, String stackTrace, Environment environment, 
    Component component) {

    
}
