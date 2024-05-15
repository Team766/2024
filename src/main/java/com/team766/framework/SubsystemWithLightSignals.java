package com.team766.framework;

public abstract class SubsystemWithLightSignals<StateRecord, Goal, LightSignals>
        extends Subsystem<StateRecord, Goal> implements WithLightSignals<LightSignals> {
    private LightSignals currentLightSignals;

    /**
     *
     */
    protected abstract LightSignals updateLightSignals(StateRecord state);

    public final LightSignals getLightSignals() {
        return currentLightSignals;
    }

    @Override
    public void periodic() {
        super.periodic();
        currentLightSignals = updateLightSignals(getState());
    }
}
