package com.team766.framework;

import java.util.function.BooleanSupplier;

public class OICondition {
    private final BooleanSupplier condition;
    private boolean triggering = false;
    private boolean finishedTriggering = false;

    public OICondition(OIFragment parent, BooleanSupplier condition) {
        this.condition = condition;
        parent.register(this);
    }

    /* package */ void evaluate() {
        boolean triggeringNow = condition.getAsBoolean();
        if (triggering && !triggeringNow) {
            finishedTriggering = true;
        } else {
            finishedTriggering = false;
        }
        triggering = triggeringNow;
    }

    public boolean isTriggering() {
        return triggering;
    }

    public boolean isFinishedTriggering() {
        return finishedTriggering;
    }
}
