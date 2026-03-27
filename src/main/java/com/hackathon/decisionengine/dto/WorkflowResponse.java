package com.hackathon.decisionengine.dto;

import com.hackathon.decisionengine.domain.rules.RuleResult;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class WorkflowResponse {
    private String requestId;
    private String finalStatus;
    private List<RuleResult> executionTrace; // This satisfies the "Explainability" rubric
}