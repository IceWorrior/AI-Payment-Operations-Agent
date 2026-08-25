package com.paymentagent.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaClient{

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    private static final String MODEL = "qwen3.5:9b";

    private final HttpClient client;

    private static final ObjectMapper mapper = new ObjectMapper();

    public OllamaClient(){

        client = HttpClient.newHttpClient();
    }

    public String generate(String prompt) throws 
    IOException, InterruptedException{

        String json = "{"
                + "\"model\":\"" + MODEL + "\","
                + "\"prompt\":\"" + escapeJson(prompt) + "\","
                + "\"stream\":false"
                + "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(OLLAMA_URL))
                            .header(
                                "Content-Type",
                                "application/json"
                            )
                            .POST(
                                HttpRequest.BodyPublishers.ofString(json)
                            )
                            .build();
    
        HttpResponse<String> response = client.send(
                                            request,
                                            HttpResponse.BodyHandlers.ofString()
                                        );

        try{

            JsonNode json = mapper.readTree(response.body());

            return json
                    .get("response")
                    .asText();

        }
        catch(Exception e){

            throw new RuntimeException(
                "Failed to parse ollama response",
                e
            );
        }

    }

    private String escapeJson(String text){

        return text
                .replace("\\","\\\\")
                .replace("\"","\\\"")
                .replace("\n","\\n")
                .replace("\r","\\r");
    }

}