package com.backend.bankcards.repository;

import com.backend.bankcards.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "SELECT * FROM audit_logs WHERE user_id = :userId ORDER BY created_at DESC", nativeQuery = true)
    List<AuditLog> findHistoryByUserId(@Param("userId") Long userId);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String card, Long cardId);
}