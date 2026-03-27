package com.hackathon.decisionengine.domain.rules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    private String ruleId;
    private int priority;
    private String targetField; 
    private Operator operator;
    private String expectedValue;

    public enum Operator {
        EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, EXISTS
    }
}