package com.hackathon.decisionengine.service;

import com.hackathon.decisionengine.domain.rules.Rule;
import com.hackathon.decisionengine.domain.rules.RuleEvaluator;
import com.hackathon.decisionengine.domain.rules.RuleResult;
import com.hackathon.decisionengine.domain.state.StateMachine;
import com.hackathon.decisionengine.dto.WorkflowRequest;
import com.hackathon.decisionengine.dto.WorkflowResponse;
import com.hackathon.decisionengine.model.AuditLog;
import com.hackathon.decisionengine.model.WorkflowState;
import com.hackathon.decisionengine.repository.AuditLogRepository;
import com.hackathon.decisionengine.repository.WorkflowStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowOrchestrator {

    private final WorkflowStateRepository stateRepository;
    private final AuditLogRepository auditLogRepository;
    private final ConfigurationManager configurationManager;
    private final ExternalDataService externalDataService;

    // We instantiate our pure domain engine here
    private final RuleEvaluator ruleEvaluator = new RuleEvaluator();

    @Transactional
    public WorkflowResponse executeWorkflow(WorkflowRequest request) {
        String requestId = UUID.randomUUID().toString();

        // 1. Initialize State (PENDING -> EVALUATING)
        WorkflowState state = WorkflowState.builder()
                .requestId(requestId)
                .workflowId(request.getWorkflowId())
                .status(StateMachine.State.PENDING)
                .build();
        
        state.setStatus(StateMachine.transition(state.getStatus(), StateMachine.State.VALIDATING));
        state.setStatus(StateMachine.transition(state.getStatus(), StateMachine.State.EVALUATING));
        stateRepository.save(state);
        // ---LINE TO TRIGGER THE EXTERNAL CALL ---
        callExternalServiceWithRetry(request.getWorkflowId());

        // 2. Load Configuration (Mocked for now - we will wire the JSON loader later)
        List<Rule> rules = configurationManager.getRulesForWorkflow(request.getWorkflowId());

        // 3. Evaluate Rules (The Brain)
        List<RuleResult> trace = ruleEvaluator.evaluate(request.getPayload(), rules);

        // 4. Determine Final State
        boolean allPassed = trace.stream().allMatch(RuleResult::isPassed);
        StateMachine.State finalState = allPassed ? StateMachine.State.APPROVED : StateMachine.State.REJECTED;
        
        state.setStatus(StateMachine.transition(state.getStatus(), finalState));
        stateRepository.save(state);

        // 5. Save Audit Logs (Explainability)
        for (RuleResult result : trace) {
            AuditLog log = AuditLog.builder()
                    .requestId(requestId)
                    .ruleId(result.getRuleId())
                    .decision(result.isPassed() ? "PASS" : "FAIL")
                    .reason(result.getReason())
                    .build();
            auditLogRepository.save(log);
        }

        // 6. Return standard response
        return WorkflowResponse.builder()
                .requestId(requestId)
                .finalStatus(finalState.name())
                .executionTrace(trace)
                .build();
    }

    private void callExternalServiceWithRetry(String workflowId) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                externalDataService.fetchExternalData(workflowId);
                return; // Success! Exit the loop.
            } catch (RuntimeException e) {
                if (attempt == maxRetries) {
                    throw new IllegalStateException("Workflow failed: External service unavailable after 3 attempts.");
                }
                System.out.println("Retry attempt " + attempt + " failed. Retrying...");
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {} // Wait 1 second before retrying
            }
        }
    }




}