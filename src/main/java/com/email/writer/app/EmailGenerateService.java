package com.email.writer.app;

import org.springframework.stereotype.Service;

@Service
public class EmailGenerateService {
    public String GenerateEmailReply(EmailRequest emailRequest){
        // Build the prompt
        String prompt = BuildPrompt(emailRequest);

        // Craft a request
        

        // Do request and get response
        // Return response
    }

    private String BuildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Professional Email Reply for the following email content. Please don't generate a subject line.");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone.");
        }

        // add original email content to tone
        prompt.append("\nOriginal email content:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
