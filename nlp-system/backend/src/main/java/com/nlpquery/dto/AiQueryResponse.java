package com.nlpquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI Query Response DTO
 */
@Data
@Builder
@Schema(description = "AI Query Response")
public class AiQueryResponse {
    
    @Schema(description = "Whether the query was successful")
    private Boolean success;
    
    @Schema(description = "Original query content")
    private String query;
    
    @Schema(description = "Generated SQL statement")
    private String sql;
    
    @Schema(description = "Column list")
    private List<String> columns;
    
    @Schema(description = "Table data")
    private List<Map<String, Object>> tableData;
    
    @Schema(description = "Chart data")
    private List<Map<String, Object>> chartData;
    
    @Schema(description = "Result count")
    private Integer resultCount;
    
    @Schema(description = "Execution time in milliseconds")
    private Long executionTime;
    
    @Schema(description = "Error message if failed")
    private String errorMessage;
    
    @Schema(description = "Data source description")
    private String dataSource;
    
    @Schema(description = "Suggested chart type")
    private String suggestedChartType;
}
