package com.nlpquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI Query Request DTO
 */
@Data
@Schema(description = "AI Query Request")
public class AiQueryRequest {
    
    @NotBlank(message = "Query content cannot be empty")
    @Size(max = 500, message = "Query content cannot exceed 500 characters")
    @Schema(description = "User query content", example = "Show me all users created in the last month")
    private String query;
    
    @Schema(description = "Whether chart data is needed", example = "true")
    private Boolean needChart = true;
    
    @Schema(description = "Result limit", example = "100")
    private Integer limit = 100;
}
