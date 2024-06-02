package com.team766.framework;

import com.google.common.reflect.TypeToken;
import com.team766.framework.Statuses.StatusSource;
import com.team766.framework.annotations.PreventReservableFields;
import com.team766.library.ReflectionUtils;
import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.ReflectionLogging;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

@PreventReservableFields
public abstract class RobotSubsystem<StatusRecord extends Record, Goal> extends SubsystemBase
        implements LoggingBase, StatusSource, LoggableInputs {
    private StatusRecord currentStatus;
    private Goal currentGoal;
    private boolean goalChanged = false;

    protected Category loggerCategory = Category.MECHANISMS;

    private final List<Field> fields = new ArrayList<>();

    public RobotSubsystem() {
        for (Class<?> clazz = this.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            for (var field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(StateVariable.class)) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
        }
    }

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

            Logger.processInputs(getName(), this);

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
        goalChanged = !newGoal.equals(currentGoal);
        currentGoal = newGoal;
    }

    public final Goal getGoal() {
        return currentGoal;
    }

    public final StatusRecord getStatus() {
        return currentStatus;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void fromLog(LogTable table) {
        goalChanged = table.get("goalChanged", goalChanged);
        try {
            currentGoal = (Goal) ReflectionLogging.fromLog(getGoalClass(), "currentGoal", table);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        try {
            currentStatus = (StatusRecord)
                    ReflectionLogging.fromLog(getStatusClass(), "currentStatus", table);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        for (var field : fields) {
            try {
                var value =
                        ReflectionLogging.fromLog(field.getGenericType(), field.getName(), table);
                field.set(this, value);
            } catch (Exception ex) {
                LoggerExceptionUtils.logException(ex);
            }
        }
    }

    @Override
    public void toLog(LogTable table) {
        table.put("goalChanged", goalChanged);
        try {
            ReflectionLogging.toLog(currentGoal, getGoalClass(), "currentGoal", table);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        try {
            ReflectionLogging.toLog(currentStatus, getStatusClass(), "currentStatus", table);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
        for (var field : fields) {
            try {
                Object value = field.get(this);
                ReflectionLogging.toLog(value, field.getGenericType(), field.getName(), table);
            } catch (Exception ex) {
                LoggerExceptionUtils.logException(ex);
            }
        }
    }

    private Class<StatusRecord> statusClass = null;

    @SuppressWarnings("unchecked")
    private Class<StatusRecord> getStatusClass() {
        if (statusClass == null) {
            statusClass = (Class<StatusRecord>) ReflectionUtils.getRawType(
                    ReflectionUtils.getTypeArguments(
                            TypeToken.of(this.getClass()).getSupertype(RobotSubsystem.class))[0]);
        }
        return statusClass;
    }

    private Class<Goal> goalClass = null;

    @SuppressWarnings("unchecked")
    protected Class<Goal> getGoalClass() {
        if (goalClass == null) {
            goalClass = (Class<Goal>) ReflectionUtils.getRawType(
                    ReflectionUtils.getTypeArguments(
                            TypeToken.of(this.getClass()).getSupertype(RobotSubsystem.class))[1]);
        }
        return goalClass;
    }
}
