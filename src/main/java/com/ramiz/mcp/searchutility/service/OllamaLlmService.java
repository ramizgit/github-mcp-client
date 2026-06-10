package com.ramiz.mcp.searchutility.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramiz.mcp.searchutility.models.OllamaRequest;
import com.ramiz.mcp.searchutility.models.OllamaResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaLlmService
        implements LlmService {

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private final ObjectMapper mapper =
            new ObjectMapper();

    @Override
    public String ask(String prompt) {

        try {

            OllamaRequest requestBody =
                    new OllamaRequest(
                            "gemma3:4b",
                            prompt,
                            false
                    );

            String json =
                    mapper.writeValueAsString(
                            requestBody
                    );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            "http://localhost:11434/api/generate"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(json)
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            OllamaResponse ollamaResponse =
                    mapper.readValue(
                            response.body(),
                            OllamaResponse.class
                    );

            return ollamaResponse.response();

        }
        catch (Exception e) {

            throw new RuntimeException(e);
        }
    }
}
