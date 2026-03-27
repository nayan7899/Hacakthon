package com.hackathon.decisionengine.controller;

import com.hackathon.decisionengine.dto.WorkflowRequest;
import com.hackathon.decisionengine.dto.WorkflowResponse;
import com.hackathon.decisionengine.service.WorkflowOrchestrator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkflowControllerTest {

    @Mock
    private WorkflowOrchestrator workflowOrchestrator;

    @InjectMocks
    private WorkflowController workflowController;

    @Test
    void testExecuteWorkflow_Success() {
        // Prepare test data
        Map<String, Object> payload = new HashMap<>();
        payload.put("monthlyIncome", "5000");
        payload.put("creditScore", "720");

        WorkflowRequest request = new WorkflowRequest();
        request.setWorkflowId("loan_approval");
        request.setPayload(payload);

        WorkflowResponse expectedResponse = WorkflowResponse.builder()
                .requestId(UUID.randomUUID().toString())
                .finalStatus("APPROVED")
                .build();

        when(workflowOrchestrator.executeWorkflow(any(WorkflowRequest.class))).thenReturn(expectedResponse);

        // Perform the test
        ResponseEntity<WorkflowResponse> response = workflowController.executeWorkflow(request);
        WorkflowResponse actualResponse = response.getBody();

        // Verify results
        assertNotNull(actualResponse);
        assertEquals("APPROVED", actualResponse.getFinalStatus());
        assertNotNull(actualResponse.getRequestId());
        assertEquals(expectedResponse.getRequestId(), actualResponse.getRequestId());
    }

    @Test
    void testExecuteWorkflow_Rejected() {
        // Prepare test data
        Map<String, Object> payload = new HashMap<>();
        payload.put("monthlyIncome", "1000");
        payload.put("creditScore", "500");

        WorkflowRequest request = new WorkflowRequest();
        request.setWorkflowId("loan_approval");
        request.setPayload(payload);

        WorkflowResponse expectedResponse = WorkflowResponse.builder()
                .requestId(UUID.randomUUID().toString())
                .finalStatus("REJECTED")
                .build();

        when(workflowOrchestrator.executeWorkflow(any(WorkflowRequest.class))).thenReturn(expectedResponse);

        // Perform the test
        ResponseEntity<WorkflowResponse> response = workflowController.executeWorkflow(request);
        WorkflowResponse actualResponse = response.getBody();

        // Verify results
        assertNotNull(actualResponse);
        assertEquals("REJECTED", actualResponse.getFinalStatus());
        assertNotNull(actualResponse.getRequestId());
    }
}