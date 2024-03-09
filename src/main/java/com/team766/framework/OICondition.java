package com.team766.framework;

import java.util.function.BooleanSupplier;

public class OICondition {
    private final BooleanSupplier condition;
    private boolean triggered;

    public OICondition(BooleanSupplier condition) {
        this.condition = condition;
        this.triggered = false;
    }

    public boolean isTriggering() {
        if (condition.getAsBoolean()) {
            triggered = true;
            return true;
        }
        return false;
    }

    public boolean isFinishedTriggering() {
        if (triggered && !condition.getAsBoolean()) {
            triggered = false;
            return true;
        }
        return false;
    }
}
