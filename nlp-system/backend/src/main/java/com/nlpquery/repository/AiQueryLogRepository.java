package com.nlpquery.repository;

import com.nlpquery.entity.AiQueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Query Log Repository
 */
@Repository
public interface AiQueryLogRepository extends JpaRepository<AiQueryLog, Long> {
    
    /**
     * Find top 10 query logs by username ordered by query time descending
     */
    List<AiQueryLog> findTop10ByUsernameOrderByQueryTimeDesc(String username);
    
    /**
     * Find query logs by username and status
     */
    List<AiQueryLog> findByUsernameAndStatus(String username, String status);
    
    /**
     * Count successful queries by username
     */
    long countByUsernameAndStatus(String username, String status);
}
