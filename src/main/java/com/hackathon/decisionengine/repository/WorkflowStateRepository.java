package com.hackathon.decisionengine.repository;

import com.hackathon.decisionengine.model.WorkflowState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowStateRepository extends JpaRepository<WorkflowState, String> {
}