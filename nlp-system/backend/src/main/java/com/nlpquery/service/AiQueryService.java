package com.nlpquery.service;

import com.nlpquery.dto.AiQueryRequest;
import com.nlpquery.dto.AiQueryResponse;
import com.nlpquery.entity.AiQueryLog;
import com.nlpquery.repository.AiQueryLogRepository;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI Query Service
 */
@Slf4j
@Service
public class AiQueryService {
    
    private final GeminiApiClient geminiApiClient;
    private final DataDictionaryService dataDictionaryService;
    private final JdbcTemplate jdbcTemplate;
    private final AiQueryLogRepository queryLogRepository;
    
    public AiQueryService(GeminiApiClient geminiApiClient,
                         DataDictionaryService dataDictionaryService,
                         JdbcTemplate jdbcTemplate,
                         AiQueryLogRepository queryLogRepository) {
        this.geminiApiClient = geminiApiClient;
        this.dataDictionaryService = dataDictionaryService;
        this.jdbcTemplate = jdbcTemplate;
        this.queryLogRepository = queryLogRepository;
    }
    
    /**
     * Process AI query request
     */
    @Transactional
    public AiQueryResponse processQuery(AiQueryRequest request) {
        String username = getCurrentUsername();
        long startTime = System.currentTimeMillis();
        
        // Record query start
        AiQueryLog queryLog = new AiQueryLog();
        queryLog.setUserQuery(request.getQuery());
        queryLog.setUsername(username);
        queryLog.setQueryTime(LocalDateTime.now());
        queryLog.setStatus("PROCESSING");
        
        try {
            // 1. Generate SQL
            String sql = generateSqlFromQuery(request.getQuery());
            queryLog.setGeneratedSql(sql);
            
            // 2. Validate and security check
            validateSql(sql);
            
            // 3. Add permission filter
            String filteredSql = addPermissionFilter(sql, username);
            queryLog.setExecutedSql(filteredSql);
            
            // 4. Execute query
            List<Map<String, Object>> tableData = executeQuery(filteredSql);
            
            // 5. Generate chart data
            List<Map<String, Object>> chartData = generateChartData(tableData, request.getQuery());
            
            // 6. Get column information
            List<String> columns = getColumns(tableData);
            
            // 7. Record success
            queryLog.setStatus("SUCCESS");
            queryLog.setResultCount(tableData.size());
            queryLog.setExecutionTime(System.currentTimeMillis() - startTime);
            queryLogRepository.save(queryLog);
            
            return AiQueryResponse.builder()
                    .success(true)
                    .query(request.getQuery())
                    .sql(filteredSql)
                    .columns(columns)
                    .tableData(tableData)
                    .chartData(chartData)
                    .resultCount(tableData.size())
                    .executionTime(System.currentTimeMillis() - startTime)
                    .build();
                    
        } catch (Exception e) {
            log.error("AI query failed", e);
            
            // Record error
            queryLog.setStatus("ERROR");
            queryLog.setErrorMessage(e.getMessage());
            queryLog.setExecutionTime(System.currentTimeMillis() - startTime);
            queryLogRepository.save(queryLog);
            
            return AiQueryResponse.builder()
                    .success(false)
                    .query(request.getQuery())
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
    
    /**
     * Use AI to generate SQL
     */
    private String generateSqlFromQuery(String userQuery) throws Exception {
        String databaseSchema = dataDictionaryService.getDatabaseSchema();
        String queryExamples = dataDictionaryService.getQueryExamples();
        
        String prompt = String.format("""
            You are a professional SQL generation expert. Please generate correct PostgreSQL query statements based on user's natural language requirements.
            
            %s
            
            %s
            
            Important Rules:
            1. Only return SQL statement, no other text explanation
            2. Use PostgreSQL syntax
            3. Use double quotes for table and column names (e.g., "table_name"."column_name")
            4. Use CURRENT_DATE function for date queries
            5. Limit query results to maximum 1000 records (use LIMIT 1000)
            6. Do not use DROP, DELETE, UPDATE, INSERT or other modification statements
            7. All tables are in the public schema, use table names directly without schema prefix
            
            User requirement: %s
            
            Please generate the corresponding SQL query statement:
            """, databaseSchema, queryExamples, userQuery);
        
        String response = geminiApiClient.generateContent(prompt);
        log.info("Gemini API raw response for query '{}': {}", userQuery, response);
        
        if (response == null || response.trim().isEmpty()) {
            throw new Exception("AI API returned empty response");
        }
        
        // Extract SQL statement (remove possible formatting marks)
        String sql = extractSqlFromResponse(response);
        
        if (sql == null || sql.trim().isEmpty()) {
            throw new Exception("Unable to extract valid SQL statement from AI response");
        }
        
        log.info("Generated SQL for query '{}': {}", userQuery, sql);
        return sql;
    }
    
    /**
     * Extract SQL from AI response
     */
    private String extractSqlFromResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return null;
        }
        
        String sql = response.trim();
        log.debug("Original AI response: {}", sql);
        
        // Remove markdown code block markers
        sql = sql.replaceAll("```sql", "").replaceAll("```", "").trim();
        
        // Remove common AI response prefixes
        sql = sql.replaceAll("^(Here is|The following is|SQL query statement as follows|Query statement is|Generated SQL is)[:：]?\\s*", "");
        
        // If contains multiple lines, only take the first SELECT statement
        Pattern pattern = Pattern.compile("(SELECT.*?)(?=;|$)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            sql = matcher.group(1);
        }
        
        // Remove trailing semicolon and extra whitespace
        sql = sql.replaceAll(";\\s*$", "").trim();
        
        // Ensure SQL is not empty
        if (sql.isEmpty()) {
            log.warn("Extracted SQL is empty from response: {}", response);
            return null;
        }
        
        log.debug("Extracted SQL: {}", sql);
        return sql;
    }
    
    /**
     * Validate SQL security
     */
    private void validateSql(String sql) throws Exception {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("Generated SQL cannot be empty");
        }
        
        String trimmedSql = sql.trim();
        log.info("Validating SQL: {}", trimmedSql);
        
        // 1. Parse SQL syntax
        try {
            Statement statement = CCJSqlParserUtil.parse(trimmedSql);
            if (!(statement instanceof Select)) {
                throw new IllegalArgumentException("Only SELECT queries are allowed");
            }
        } catch (JSQLParserException e) {
            log.error("SQL parsing failed for: {}", trimmedSql, e);
            throw new IllegalArgumentException("SQL syntax error: " + e.getMessage());
        }
        
        // 2. Check dangerous keywords
        String upperSql = trimmedSql.toUpperCase();
        String[] dangerousKeywords = {
            "DROP", "DELETE", "UPDATE", "INSERT", "CREATE", "ALTER", 
            "TRUNCATE", "EXEC", "EXECUTE", "GRANT", "REVOKE"
        };
        
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                throw new IllegalArgumentException("Forbidden SQL keyword: " + keyword);
            }
        }
        
        // 3. Check if LIMIT restriction exists (add automatically if missing)
        if (!upperSql.contains("LIMIT")) {
            log.warn("SQL missing LIMIT clause, it will be added automatically");
        }
    }
    
    /**
     * Add permission filter conditions
     */
    private String addPermissionFilter(String sql, String username) {
        // Here you can add additional WHERE conditions based on user permissions
        // For example: department filtering, tenant filtering, etc.
        String filteredSql = sql;
        
        // Ensure SQL has LIMIT restriction, add if missing
        String upperSql = filteredSql.toUpperCase();
        if (!upperSql.contains("LIMIT")) {
            filteredSql += " LIMIT 1000";
            log.info("Added LIMIT clause to SQL: {}", filteredSql);
        }
        
        return filteredSql;
    }
    
    /**
     * Execute query
     */
    private List<Map<String, Object>> executeQuery(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("SQL execution error: {}", sql, e);
            throw new RuntimeException("Query execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Generate chart data
     */
    private List<Map<String, Object>> generateChartData(List<Map<String, Object>> tableData, String userQuery) {
        // Simple chart data generation logic
        // In actual applications, different types of charts can be generated based on query type and data characteristics
        if (tableData.isEmpty()) {
            return new ArrayList<>();
        }
        
        // If query involves statistics or aggregation, try to generate chart data
        if (userQuery.toLowerCase().contains("count") || userQuery.toLowerCase().contains("sum") || 
            userQuery.toLowerCase().contains("avg") || userQuery.toLowerCase().contains("statistics")) {
            return tableData; // Use table data directly as chart data
        }
        
        // Default return first 10 records in chart form
        return tableData.stream()
                .limit(10)
                .collect(ArrayList::new, (list, item) -> list.add(item), List::addAll);
    }
    
    /**
     * Get column list
     */
    private List<String> getColumns(List<Map<String, Object>> tableData) {
        if (tableData.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tableData.get(0).keySet());
    }
    
    /**
     * Get current username
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }
}
