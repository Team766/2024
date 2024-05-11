package com.team766.framework3;

public abstract class Subsystem<StateRecord, Goal, LightSignals> extends edu.wpi.first.wpilibj2.command.SubsystemBase {
    private StateRecord currentState;
    private Goal currentGoal;
    private LightSignals currentLightSignals;

    /**
     * Collect all of the information about the current state (i.e. from sensors)
     * and return it in a StateRecord.
     */
    protected abstract StateRecord updateState();

    /**
     * TODO: Should this just run the Runnable code directly instead of returning it for the framework to run?
     */
    protected abstract Runnable dispatch(StateRecord stateRecord, Goal goal);

    /**
     *
     */
    protected abstract LightSignals updateLightSignals(StateRecord stateRecord);

    @Override
    public void periodic() {
        currentState = updateState();
        final Runnable behavior = dispatch(currentState, currentGoal);
        currentLightSignals = updateLightSignals(currentState);
        behavior.run();
    }

    public final void setGoal(Goal newGoal) {
        currentGoal = newGoal;
    }

    public final StateRecord getState() {
        return currentState;
    }

    public final LightSignals getLightSignals() {
        return currentLightSignals;
    }
}
