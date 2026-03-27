package com.hackathon.decisionengine.controller;

import com.hackathon.decisionengine.dto.WorkflowRequest;
import com.hackathon.decisionengine.dto.WorkflowResponse;
import com.hackathon.decisionengine.service.WorkflowOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowOrchestrator workflowOrchestrator;

    @PostMapping("/execute")
    public ResponseEntity<WorkflowResponse> executeWorkflow(@Valid @RequestBody WorkflowRequest request) {
        WorkflowResponse response = workflowOrchestrator.executeWorkflow(request);
        return ResponseEntity.ok(response);
    }
}