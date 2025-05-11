package com.email.writer.app;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGenerateService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGenerateService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String GenerateEmailReply(EmailRequest emailRequest){
        // Build the prompt
        String prompt = BuildPrompt(emailRequest);

        // Craft a request
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );

        // Do request and get response
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Return extracted response
        return extractResponseContent(response);


    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e){
            return "Error: " + e.getMessage();
        }
    }

    private String BuildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Revise the email for the following email content. Please don't generate a subject line. Please don't generate anything else besides the email content.");
        if (emailRequest.getContext() != null && !emailRequest.getContext().isEmpty()){
            prompt.append("This is the context of the email: ").append(emailRequest.getContext());
        }

        // add original email content to tone
        prompt.append("\nOriginal email content:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
