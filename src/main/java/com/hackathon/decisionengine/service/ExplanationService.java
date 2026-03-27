package com.hackathon.decisionengine.service;

import com.hackathon.decisionengine.model.AuditLog;
import com.hackathon.decisionengine.repository.AuditLogRepository; // Added the correct import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExplanationService {

    // This was showing red because the import was missing
    private final AuditLogRepository auditLogRepository;

    /**
     * Generates a human-readable explanation for the workflow decision
     */
    public String generateExplanation(String requestId) {
        List<AuditLog> auditLogs = auditLogRepository.findByRequestId(requestId);
        
        if (auditLogs.isEmpty()) {
            return "No audit logs found for request: " + requestId;
        }

        // Group by decision type
        List<AuditLog> passedRules = auditLogs.stream()
                .filter(log -> "PASS".equals(log.getDecision()))
                .collect(Collectors.toList());
        
        List<AuditLog> failedRules = auditLogs.stream()
                .filter(log -> "FAIL".equals(log.getDecision()))
                .collect(Collectors.toList());

        StringBuilder explanation = new StringBuilder();
        explanation.append("Decision Explanation for Request: ").append(requestId).append("\n\n");

        // Overall decision
        String overallDecision = failedRules.isEmpty() ? "APPROVED" : "REJECTED";
        explanation.append("Overall Decision: ").append(overallDecision).append("\n\n");

        // Passed rules
        if (!passedRules.isEmpty()) {
            explanation.append("✓ PASSED VALIDATIONS:\n");
            for (AuditLog log : passedRules) {
                explanation.append("  • ").append(formatRuleExplanation(log))
                        .append(" [PASSED]\n");
            }
            explanation.append("\n");
        }

        // Failed rules
        if (!failedRules.isEmpty()) {
            explanation.append("✗ FAILED VALIDATIONS:\n");
            for (AuditLog log : failedRules) {
                explanation.append("  • ").append(formatRuleExplanation(log))
                        .append(" [FAILED]\n");
            }
            explanation.append("\n");
        }

        // Summary
        explanation.append("Summary: ").append(passedRules.size()).append(" rules passed, ")
                .append(failedRules.size()).append(" rules failed.");

        return explanation.toString();
    }

    /**
     * Formats a rule explanation from audit log. 
     * Note: Matches the Rule IDs used in your loan_approval.json
     */
    private String formatRuleExplanation(AuditLog log) {
        String ruleId = log.getRuleId();
        String reason = log.getReason();
        
        switch (ruleId) {
            case "income_check":
                return "Monthly Income verification";
            case "credit_score_check":
                return "Credit Score threshold check";
            case "employment_status":
                return "Current Employment status verification";
            default:
                return ruleId + (reason != null ? ": " + reason : "");
        }
    }

    /**
     * Generates a detailed technical explanation for developers
     */
    public String generateTechnicalExplanation(String requestId) {
        List<AuditLog> auditLogs = auditLogRepository.findByRequestId(requestId);
        
        StringBuilder technical = new StringBuilder();
        technical.append("Technical Audit Log for Request: ").append(requestId).append("\n");
        technical.append("=".repeat(50)).append("\n\n");

        for (AuditLog log : auditLogs) {
            technical.append("Rule ID: ").append(log.getRuleId()).append("\n");
            technical.append("Decision: ").append(log.getDecision()).append("\n");
            technical.append("Reason: ").append(log.getReason() != null ? log.getReason() : "N/A").append("\n");
            technical.append("Timestamp: ").append(log.getCreatedAt()).append("\n");
            technical.append("-".repeat(30)).append("\n");
        }

        return technical.toString();
    }
}