package com.nlpquery.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * AI Query Log Entity
 */
@Data
@Entity
@Table(name = "ai_query_log")
public class AiQueryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", length = 100)
    private String username;
    
    @Column(name = "user_query", columnDefinition = "TEXT")
    private String userQuery;
    
    @Column(name = "generated_sql", columnDefinition = "TEXT")
    private String generatedSql;
    
    @Column(name = "executed_sql", columnDefinition = "TEXT")
    private String executedSql;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "result_count")
    private Integer resultCount;
    
    @Column(name = "execution_time")
    private Long executionTime;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(name = "query_time")
    private LocalDateTime queryTime;
    
    @Column(name = "feedback")
    private String feedback; // User feedback: CORRECT, INCORRECT, PARTIAL
    
    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;
}
