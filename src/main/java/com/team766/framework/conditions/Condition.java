package com.team766.framework.conditions;

public interface Condition {
    ConditionState getState();

    default boolean isNotTriggering() {
        final var state = getState();
        return state == ConditionState.IsNotTriggering
                || state == ConditionState.IsFinishedTriggering;
    }

    default boolean isTriggering() {
        final var state = getState();
        return state == ConditionState.IsTriggering || state == ConditionState.IsNewlyTriggering;
    }

    default boolean isNewlyTriggering() {
        return getState() == ConditionState.IsNewlyTriggering;
    }

    default boolean isFinishedTriggering() {
        return getState() == ConditionState.IsFinishedTriggering;
    }
}
