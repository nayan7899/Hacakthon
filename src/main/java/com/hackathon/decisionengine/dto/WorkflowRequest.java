package com.hackathon.decisionengine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.Map;

@Data
public class WorkflowRequest {
    
    @NotBlank(message = "Workflow ID is required")
    private String workflowId;

    @NotEmpty(message = "Payload cannot be empty")
    private Map<String, Object> payload; // The dynamic JSON data (e.g., creditScore, income)
}