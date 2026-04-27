package com.nosmoke.nexus_ai.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nosmoke.nexus_ai.ai.repository.AiRepository;
import com.nosmoke.nexus_ai.model.AiSolutions;
import com.nosmoke.nexus_ai.model.ErrorLog;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiRepository aiRepository;
    private final RestTemplate restTemplate;


    public AiSolutions analyzeError(ErrorLog errorLog){
        String prompt = "I want you to analyze this error using all the information below and give the user a solution based on his needs. \n" 
            + "Use the error message to gather information about what the problem is " + errorLog.getErrorMessage() + 
            "\n .Use the stack trace to have all the information of what is the context about the problem: " + errorLog.getStackTrace()
            + "\n. What kind of error it is based on the context: " + errorLog.getLevel() 
            + "\n. Learn where the problem resides, analizy the error to see if it belongs in the backend, frontend or both, also the user might give you some info in that " 
            + errorLog.getComponent()
            + "\n. Finally, based on the error log enviroment, if its prod, dev or staging. Base your solution around all the information: "
            + errorLog.getEnvironment();

            String requestBody = """
                    {
                        "contents": [{"parts": [{"text": "%s"}]}]
                    }
                    """.formatted(prompt);
    }

    
}
