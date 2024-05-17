package com.team766.framework.conditions;

public enum ConditionState implements Condition {
    IsNotTriggering,
    IsTriggering,
    IsNewlyTriggering,
    IsFinishedTriggering;

    public static ConditionState make(boolean wasTriggering, boolean isTriggering) {
        return wasTriggering
                ? (isTriggering ? ConditionState.IsTriggering : ConditionState.IsFinishedTriggering)
                : (isTriggering
                        ? ConditionState.IsNewlyTriggering
                        : ConditionState.IsNotTriggering);
    }

    public final ConditionState getState() {
        return this;
    }
}
