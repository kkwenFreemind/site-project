package com.nlpquery.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlpquery.config.AiQueryConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Gemini API Client Service
 */
@Slf4j
@Service
public class GeminiApiClient {
    
    private final AiQueryConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private OkHttpClient httpClient;
    
    public GeminiApiClient(AiQueryConfig config) {
        this.config = config;
    }
    
    /**
     * Initialize HTTP client
     */
    private OkHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(config.getTimeout(), TimeUnit.SECONDS)
                    .readTimeout(config.getTimeout(), TimeUnit.SECONDS)
                    .writeTimeout(config.getTimeout(), TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return httpClient;
    }
    
    /**
     * Call Gemini API to generate content
     */
    public String generateContent(String prompt) throws IOException {
        String url = String.format("%s/v1beta/models/%s:generateContent?key=%s", 
                config.getBaseUrl(), config.getModel(), config.getApiKey());
        
        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));
        
        // Set generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.1);
        generationConfig.put("topK", 1);
        generationConfig.put("topP", 1);
        generationConfig.put("maxOutputTokens", 2048);
        requestBody.put("generationConfig", generationConfig);
        
        String requestJson = objectMapper.writeValueAsString(requestBody);
        
        if (config.getLogQueries()) {
            log.info("Gemini API Request: {}", requestJson);
        }
        
        RequestBody body = RequestBody.create(
                requestJson, 
                MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        
        try (Response response = getHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("Gemini API error: {} - {}", response.code(), errorBody);
                throw new IOException("Gemini API call failed: " + response.code() + " - " + errorBody);
            }
            
            String responseJson = response.body().string();
            if (config.getLogQueries()) {
                log.info("Gemini API Response: {}", responseJson);
            }
            
            // Parse response
            JsonNode responseNode = objectMapper.readTree(responseJson);
            JsonNode candidatesNode = responseNode.get("candidates");
            if (candidatesNode != null && candidatesNode.isArray() && candidatesNode.size() > 0) {
                JsonNode contentNode = candidatesNode.get(0).get("content");
                if (contentNode != null) {
                    JsonNode partsNode = contentNode.get("parts");
                    if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                        return partsNode.get(0).get("text").asText();
                    }
                }
            }
            
            throw new IOException("Invalid response format from Gemini API");
        }
    }
}
