package com.team766.framework;

import static com.team766.framework.InstantProcedure.reservations;

import com.team766.framework.Statuses.StatusSource;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Objects;

public abstract class Subsystem<StatusRecord extends Record, Goal> extends SubsystemBase
        implements LoggingBase, StatusSource {
    private StatusRecord currentStatus;
    private Goal currentGoal;
    private boolean goalChanged = false;

    protected Category loggerCategory = Category.MECHANISMS;

    @Override
    public Category getLoggerCategory() {
        return loggerCategory;
    }

    /**
     * Collect information about the current state (i.e. from sensors)
     * and return it in a StatusRecord.
     */
    protected abstract StatusRecord updateState();

    /**
     *
     */
    protected abstract void dispatch(StatusRecord status, Goal goal, boolean goalChanged);

    @Override
    public final boolean isStatusActive() {
        return true;
    }

    @Override
    public final void periodic() {
        try {
            currentStatus = updateState();
            Statuses.getInstance().add(currentStatus, this);

            do {
                goalChanged = false;
                if (currentGoal != null) {
                    dispatch(currentStatus, currentGoal, goalChanged);
                }
            } while (goalChanged);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }

    public final void setGoal(Goal newGoal) {
        Objects.requireNonNull(newGoal, "Goal object must be non-null");
        currentGoal = newGoal;
        goalChanged = true;
    }

    public final InstantProcedure setGoalBehavior(Goal goal) {
        Objects.requireNonNull(goal, "Goal object must be non-null");
        return new InstantProcedure(reservations(this)) {
            @Override
            public void run() {
                setGoal(goal);
            }
        };
    }

    public final Goal getGoal() {
        return currentGoal;
    }

    public final StatusRecord getStatus() {
        return currentStatus;
    }
}
