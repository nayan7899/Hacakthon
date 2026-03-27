package com.hackathon.decisionengine.domain;

public class StateMachine {

    public enum State {
        PENDING,
        EVALUATING,
        APPROVED,
        REJECTED,
        MANUAL_REVIEW
    }

    /**
     * Enforces strict lifecycle transitions. 
     * Throws an error if an illegal move is attempted.
     */
    public static State transition(State currentState, State nextState) {
        
        // Rule 1: PENDING can ONLY go to EVALUATING
        if (currentState == State.PENDING && nextState == State.EVALUATING) {
            return nextState;
        }
        
        // Rule 2: EVALUATING can go to any of the 3 Terminal States
        if (currentState == State.EVALUATING && 
           (nextState == State.APPROVED || nextState == State.REJECTED || nextState == State.MANUAL_REVIEW)) {
            return nextState;
        }

        // Rule 3: Anything else is an illegal hack attempt or bug!
        throw new IllegalStateException(
            "Invalid transition: Cannot move from " + currentState + " to " + nextState
        );
    }
}