package com.hackathon.decisionengine.domain.state;

import java.util.Map;
import java.util.Set;

public class StateMachine {

    public enum State {
        PENDING, VALIDATING, EVALUATING, APPROVED, REJECTED, MANUAL_REVIEW, RETRY
    }

    // Define strict transition rules
    private static final Map<State, Set<State>> VALID_TRANSITIONS = Map.of(
        State.PENDING, Set.of(State.VALIDATING),
        State.VALIDATING, Set.of(State.EVALUATING, State.REJECTED),
        State.EVALUATING, Set.of(State.APPROVED, State.REJECTED, State.MANUAL_REVIEW),
        State.RETRY, Set.of(State.EVALUATING)
    );

    /**
     * Checks if a transition is allowed.
     */
    public static boolean canTransition(State current, State next) {
        return VALID_TRANSITIONS.getOrDefault(current, Set.of()).contains(next);
    }

    /**
     * Executes the transition or throws an exception if invalid.
     */
    public static State transition(State current, State next) {
        if (!canTransition(current, next)) {
            throw new IllegalStateException(
                String.format("Invalid state transition from %s to %s", current, next)
            );
        }
        return next;
    }
}