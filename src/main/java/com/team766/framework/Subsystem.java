package com.team766.framework;

import static com.team766.framework.InstantProcedure.reservations;

import com.team766.logging.Category;

public abstract class Subsystem<StateRecord, Goal>
        extends edu.wpi.first.wpilibj2.command.SubsystemBase
        implements LoggingBase, WithState<StateRecord> {
    private StateRecord currentState;
    private Goal currentGoal;

    protected Category loggerCategory = Category.MECHANISMS;

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    /**
     * Collect all of the information about the current state (i.e. from sensors)
     * and return it in a StateRecord.
     */
    protected abstract StateRecord updateState();

    /**
     *
     */
    protected abstract void dispatch(StateRecord state, Goal goal);

    @Override
    public void periodic() {
        currentState = updateState();

        Goal initialGoal;
        do {
            initialGoal = currentGoal;
            dispatch(currentState, currentGoal);
        } while (currentGoal != initialGoal);
    }

    public final void setGoal(Goal newGoal) {
        currentGoal = newGoal;
    }

    public final InstantProcedure setGoalBehavior(Goal goal) {
        return new InstantProcedure(reservations(this)) {
            @Override
            public void run() {
                setGoal(goal);
            }
        };
    }

    protected final Goal getGoal() {
        return currentGoal;
    }

    public final StateRecord getState() {
        return currentState;
    }
}
