package com.nlpquery.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI Query Configuration Properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai.gemini")
public class AiQueryConfig {
    
    /**
     * Gemini API Key
     */
    private String apiKey;
    
    /**
     * API Base URL
     */
    private String baseUrl = "https://generativelanguage.googleapis.com";
    
    /**
     * Model Name
     */
    private String model = "gemini-1.5-flash";
    
    /**
     * Connection timeout in seconds
     */
    private Integer timeout = 30;
    
    /**
     * Maximum retry attempts
     */
    private Integer maxRetries = 3;
    
    /**
     * Whether to log queries
     */
    private Boolean logQueries = true;
}
