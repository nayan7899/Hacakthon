package com.hackathon.decisionengine.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StateMachineTest {

    @Test
    void testValidTransition_PendingToEvaluating() {
        StateMachine.State nextState = StateMachine.transition(StateMachine.State.PENDING, StateMachine.State.EVALUATING);
        assertEquals(StateMachine.State.EVALUATING, nextState, "Should successfully transition from PENDING to EVALUATING");
    }

    @Test
    void testValidTransitions_EvaluatingToTerminal() {
        assertEquals(StateMachine.State.APPROVED, 
            StateMachine.transition(StateMachine.State.EVALUATING, StateMachine.State.APPROVED));
            
        assertEquals(StateMachine.State.REJECTED, 
            StateMachine.transition(StateMachine.State.EVALUATING, StateMachine.State.REJECTED));
            
        assertEquals(StateMachine.State.MANUAL_REVIEW, 
            StateMachine.transition(StateMachine.State.EVALUATING, StateMachine.State.MANUAL_REVIEW));
    }

    @Test
    void testInvalidTransition_SkipEvaluating_ShouldFail() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            StateMachine.transition(StateMachine.State.PENDING, StateMachine.State.APPROVED);
        });
        assertTrue(exception.getMessage().contains("Invalid transition"));
    }

    @Test
    void testInvalidTransition_ChangeTerminalState_ShouldFail() {
        assertThrows(IllegalStateException.class, () -> {
            StateMachine.transition(StateMachine.State.APPROVED, StateMachine.State.REJECTED);
        });

        assertThrows(IllegalStateException.class, () -> {
            StateMachine.transition(StateMachine.State.APPROVED, StateMachine.State.EVALUATING);
        });
    }
}