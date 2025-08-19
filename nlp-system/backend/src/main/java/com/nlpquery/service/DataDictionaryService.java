package com.nlpquery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Data Dictionary Service
 * Provides database structure information for AI to generate SQL
 */
@Slf4j
@Service
public class DataDictionaryService {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DataDictionaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Get database schema description
     */
    public String getDatabaseSchema() {
        StringBuilder schema = new StringBuilder();
        schema.append("Database Schema Information:\n\n");
        
        try {
            // Get all table information
            List<Map<String, Object>> tables = getAllTables();
            
            for (Map<String, Object> table : tables) {
                String tableName = (String) table.get("table_name");
                String tableComment = (String) table.get("table_comment");
                
                schema.append("Table: ").append(tableName).append("\n");
                if (tableComment != null && !tableComment.trim().isEmpty()) {
                    schema.append("Purpose: ").append(tableComment).append("\n");
                }
                
                // Get table column information
                List<Map<String, Object>> columns = getTableColumns(tableName);
                schema.append("Columns:\n");
                
                for (Map<String, Object> column : columns) {
                    String columnName = (String) column.get("column_name");
                    String dataType = (String) column.get("data_type");
                    String columnComment = (String) column.get("column_comment");
                    Boolean isNullable = "YES".equals(column.get("is_nullable"));
                    
                    schema.append("  - ").append(columnName)
                           .append(" (").append(dataType).append(")");
                    
                    if (!isNullable) {
                        schema.append(" [Required]");
                    }
                    
                    if (columnComment != null && !columnComment.trim().isEmpty()) {
                        schema.append(" - ").append(columnComment);
                    }
                    
                    schema.append("\n");
                }
                schema.append("\n");
            }
            
        } catch (Exception e) {
            log.error("Error getting database schema", e);
            schema.append("Unable to retrieve database schema: ").append(e.getMessage());
        }
        
        return schema.toString();
    }
    
    /**
     * Get all tables information
     */
    private List<Map<String, Object>> getAllTables() {
        String sql = """
            SELECT 
                t.table_name,
                COALESCE(obj_description(c.oid), '') as table_comment
            FROM information_schema.tables t
            LEFT JOIN pg_class c ON c.relname = t.table_name
            LEFT JOIN pg_namespace n ON n.oid = c.relnamespace
            WHERE t.table_schema = 'public'
            AND t.table_type = 'BASE TABLE'
            AND n.nspname = 'public'
            ORDER BY t.table_name
        """;
        
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * Get column information for specified table
     */
    private List<Map<String, Object>> getTableColumns(String tableName) {
        String sql = """
            SELECT 
                c.column_name,
                c.data_type,
                c.is_nullable,
                COALESCE(col_description(pgc.oid, c.ordinal_position), '') as column_comment
            FROM information_schema.columns c
            LEFT JOIN pg_class pgc ON pgc.relname = c.table_name
            LEFT JOIN pg_namespace n ON n.oid = pgc.relnamespace
            WHERE c.table_schema = 'public'
            AND c.table_name = ?
            AND n.nspname = 'public'
            ORDER BY c.ordinal_position
        """;
        
        return jdbcTemplate.queryForList(sql, tableName);
    }
    
    /**
     * Get common query examples
     */
    public String getQueryExamples() {
        return """
            Common Query Examples:
            
            1. User Management:
               Input: "Show all active users"
               Description: Query users with active status
            
            2. Data Statistics:
               Input: "Count records by status"
               Description: Count records grouped by status
            
            3. Time-based Queries:
               Input: "Show records created in the last month"
               Description: Query records with date filtering
            
            4. Aggregation Queries:
               Input: "Calculate average values by category"
               Description: Aggregate data by category
            
            Notes:
            - All queries will be automatically filtered by user permissions
            - Query results will be limited based on user access level
            - Supports natural language descriptions in English and Chinese
            """;
    }
}
