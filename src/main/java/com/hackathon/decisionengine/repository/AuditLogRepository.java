package com.hackathon.decisionengine.repository;

import com.hackathon.decisionengine.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Find all audit logs for a specific request ID
     * @param requestId The request ID to search for
     * @return List of audit logs for the request
     */
    List<AuditLog> findByRequestId(String requestId);
    
    /**
     * Find audit logs by decision type
     * @param decision The decision (PASS/FAIL)
     * @return List of audit logs with the specified decision
     */
    List<AuditLog> findByDecision(String decision);
    
    /**
     * Find audit logs by rule ID
     * @param ruleId The rule ID to search for
     * @return List of audit logs for the rule
     */
    List<AuditLog> findByRuleId(String ruleId);
}