package com.hackathon.decisionengine.model;

import com.hackathon.decisionengine.domain.state.StateMachine.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowState {

    @Id
    @Column(name = "request_id", nullable = false, updatable = false)
    private String requestId;

    @Column(name = "workflow_id", nullable = false)
    private String workflowId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}