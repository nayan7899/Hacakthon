package com.hackathon.decisionengine.domain.rules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RuleEvaluator {

    public List<RuleResult> evaluate(Map<String, Object> inputPayload, List<Rule> rules) {
        List<RuleResult> trace = new ArrayList<>();
        
        // FIX: Create a mutable copy of the list before sorting!
        List<Rule> mutableRules = new ArrayList<>(rules);
        mutableRules.sort(Comparator.comparingInt(Rule::getPriority));

        for (Rule rule : mutableRules) {
            Object actualValueObj = inputPayload.get(rule.getTargetField());
            String actualValue = actualValueObj != null ? actualValueObj.toString() : null;
            
            RuleResult result = evaluateCondition(rule, actualValue);
            trace.add(result);

            if (!result.isPassed()) {
                break; // Fail-fast on the first broken rule
            }
        }
        return trace;
    }

    private RuleResult evaluateCondition(Rule rule, String actualValue) {
        if (actualValue == null && rule.getOperator() != Rule.Operator.EXISTS) {
            return new RuleResult(rule.getRuleId(), false, "null", "Field " + rule.getTargetField() + " is missing");
        }

        boolean passed = false;
        try {
            switch (rule.getOperator()) {
                case EQUALS -> passed = actualValue.equals(rule.getExpectedValue());
                case NOT_EQUALS -> passed = !actualValue.equals(rule.getExpectedValue());
                case GREATER_THAN -> passed = Double.parseDouble(actualValue) > Double.parseDouble(rule.getExpectedValue());
                case LESS_THAN -> passed = Double.parseDouble(actualValue) < Double.parseDouble(rule.getExpectedValue());
                case EXISTS -> passed = (actualValue != null && !actualValue.trim().isEmpty());
            }
        } catch (NumberFormatException e) {
             return new RuleResult(rule.getRuleId(), false, actualValue, "Type mismatch for numeric comparison");
        }

        String reason = passed ? "Condition met" : "Expected " + rule.getOperator() + " " + rule.getExpectedValue() + ", but got " + actualValue;
        return new RuleResult(rule.getRuleId(), passed, actualValue, reason);
    }
}