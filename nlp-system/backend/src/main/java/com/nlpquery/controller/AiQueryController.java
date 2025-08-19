package com.nlpquery.controller;

import com.nlpquery.dto.AiQueryRequest;
import com.nlpquery.dto.AiQueryResponse;
import com.nlpquery.entity.AiQueryLog;
import com.nlpquery.repository.AiQueryLogRepository;
import com.nlpquery.service.AiQueryService;
import com.nlpquery.service.DataDictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Query Controller
 */
@Slf4j
@RestController
@RequestMapping("/v1/ai")
@Tag(name = "AI Query", description = "AI Natural Language Query API")
public class AiQueryController {
    
    private final AiQueryService aiQueryService;
    private final DataDictionaryService dataDictionaryService;
    private final AiQueryLogRepository queryLogRepository;
    
    public AiQueryController(AiQueryService aiQueryService,
                           DataDictionaryService dataDictionaryService,
                           AiQueryLogRepository queryLogRepository) {
        this.aiQueryService = aiQueryService;
        this.dataDictionaryService = dataDictionaryService;
        this.queryLogRepository = queryLogRepository;
    }
    
    @PostMapping("/query")
    @Operation(summary = "AI Natural Language Query", description = "Generate SQL intelligently based on user's natural language requirements and execute query")
    public ResponseEntity<AiQueryResponse> query(@Valid @RequestBody AiQueryRequest request) {
        
        log.info("Received AI query request: {}", request.getQuery());
        
        AiQueryResponse response = aiQueryService.processQuery(request);
        
        if (response.getSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/schema")
    @Operation(summary = "Get Database Schema", description = "Get current database table structure information")
    public ResponseEntity<Map<String, String>> getDatabaseSchema() {
        String schema = dataDictionaryService.getDatabaseSchema();
        Map<String, String> result = new HashMap<>();
        result.put("schema", schema);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/examples")
    @Operation(summary = "Get Query Examples", description = "Get common query examples")
    public ResponseEntity<Map<String, String>> getQueryExamples() {
        String examples = dataDictionaryService.getQueryExamples();
        Map<String, String> result = new HashMap<>();
        result.put("examples", examples);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    @Operation(summary = "Get Query History", description = "Get current user's query history")
    public ResponseEntity<List<AiQueryLog>> getQueryHistory() {
        String username = getCurrentUsername();
        List<AiQueryLog> history = queryLogRepository.findTop10ByUsernameOrderByQueryTimeDesc(username);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/feedback/{id}")
    @Operation(summary = "Provide Query Feedback", description = "User provides feedback on query result accuracy")
    public ResponseEntity<Map<String, String>> provideFeedback(
            @Parameter(description = "Query log ID") @PathVariable Long id,
            @Parameter(description = "Feedback type") @RequestParam String feedback,
            @Parameter(description = "Feedback comment") @RequestParam(required = false) String comment) {
        
        AiQueryLog queryLog = queryLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Query record not found"));
        
        // Check if it's current user's record
        String username = getCurrentUsername();
        if (!username.equals(queryLog.getUsername())) {
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("error", "No permission to operate this record");
            return ResponseEntity.badRequest().body(errorResult);
        }
        
        queryLog.setFeedback(feedback);
        queryLog.setFeedbackComment(comment);
        queryLogRepository.save(queryLog);
        
        log.info("User {} provided feedback {} for query {}", username, feedback, id);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Feedback saved successfully");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get Query Statistics", description = "Get AI query statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        String username = getCurrentUsername();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQueries", queryLogRepository.countByUsernameAndStatus(username, "SUCCESS") + 
                                  queryLogRepository.countByUsernameAndStatus(username, "ERROR"));
        stats.put("successfulQueries", queryLogRepository.countByUsernameAndStatus(username, "SUCCESS"));
        stats.put("failedQueries", queryLogRepository.countByUsernameAndStatus(username, "ERROR"));
        
        long total = (Long) stats.get("totalQueries");
        long successful = (Long) stats.get("successfulQueries");
        double successRate = total > 0 ? (double) successful / total * 100 : 0;
        stats.put("successRate", String.format("%.1f%%", successRate));
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get current username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }
}
