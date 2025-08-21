package com.alertsystem.repository;

import com.alertsystem.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    List<LoginLog> findByUserIdOrderByLoginTimeDesc(Long userId);

    @Query("SELECT ll FROM LoginLog ll WHERE ll.user.id = :userId AND ll.loginTime >= :since")
    List<LoginLog> findRecentLoginsByUserId(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(ll) FROM LoginLog ll WHERE ll.ipAddress = :ipAddress AND ll.loginTime >= :since AND ll.status = 'FAILED'")
    long countFailedLoginsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
