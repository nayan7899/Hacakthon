package com.hackathon.decisionengine.domain.rules;

import lombok.Data;
import java.util.List;

@Data
public class WorkflowConfig {
    private String workflowId;
    private String version;
    private String description;
    private List<Rule> rules;
}