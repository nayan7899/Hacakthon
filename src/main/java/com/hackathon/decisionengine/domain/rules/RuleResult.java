package com.hackathon.decisionengine.domain.rules;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleResult {
    private final String ruleId;
    private final boolean passed;
    private final String evaluatedValue;
    private final String reason;
}