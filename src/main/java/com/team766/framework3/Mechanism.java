package com.team766.framework3;

import com.team766.framework.StackTraceUtils;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Objects;

public abstract class Mechanism<R extends Request> extends SubsystemBase implements LoggingBase {
    private boolean isRunningPeriodic = false;

    private Superstructure<?> superstructure = null;

    private R request = null;
    private boolean isRequestNew = false;

    /**
     * This Command runs when no other Command (i.e. Procedure) is reserving this Mechanism.
     * If this Mechanism defines getIdleRequest, this Command serves to apply that request.
     */
    private final class IdleCommand extends Command {
        public IdleCommand() {
            addRequirements(Mechanism.this);
        }

        @Override
        public void initialize() {
            try {
                final var r = getIdleRequest();
                if (r != null) {
                    ReservingCommand.enterCommand(this);
                    try {
                        setRequest(r);
                    } finally {
                        ReservingCommand.exitCommand(this);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LoggerExceptionUtils.logException(ex);
            }
        }

        @Override
        public boolean isFinished() {
            return false;
        }
    }

    public Mechanism() {
        setDefaultCommand(new IdleCommand());
    }

    @Override
    public Category getLoggerCategory() {
        return Category.MECHANISMS;
    }

    /**
     * Indicate that this Mechanism is part of a superstructure.
     *
     * A Mechanism in a superstructure cannot be reserved individually by Procedures (Procedures
     * should reserve the entire superstructure) and cannot have an Idle request. Only the
     * superstructure should set requests on this Mechanism in its {@link #run(R, boolean)} method.
     *
     * @param superstructure The superstructure this Mechanism is part of.
     */
    /* package */ void setSuperstructure(Superstructure<?> superstructure) {
        Objects.requireNonNull(superstructure);
        if (this.superstructure != null) {
            throw new IllegalStateException("Mechanism is already part of a superstructure");
        }
        if (this.getIdleRequest() != null) {
            throw new UnsupportedOperationException(
                    "A Mechanism contained in a superstructure cannot define an idle request. "
                            + "Use the superstructure's idle request to control the idle behavior "
                            + "of the contained Mechanisms.");
        }
        this.superstructure = superstructure;
    }

    public final void setRequest(R request) {
        Objects.requireNonNull(request);
        checkContextReservation();
        this.request = request;
        isRequestNew = true;
        log(this.getClass().getName() + " processing request: " + request);
    }

    /**
     * The request returned by this method will be set as the request for this Mechanism when the
     * Mechanism is first created.
     *
     * If this Mechanism defines getIdleRequest(), then this Initial request will only be passed to
     * the first call to run() (after that, the Idle request may take over). Otherwise, this Initial
     * request will be passed to run() until something calls setRequest() on this Mechanism.
     *
     * This method will only be called once, immediately before the first call to run(). Because it
     * is called before run(), it cannot call getMechanismStatus() or otherwise depend on the Status
     * published by this Mechanism.
     */
    protected abstract R getInitialRequest();

    /**
     * The request returned by this method will be set as the request for this Mechanism when no
     * Procedures are reserving this Mechanism. This happens when a Procedure which reserved this
     * Mechanism completes. It can also happen when a Procedure that reserves this Mechanism is
     * preempted by another Procedure, but the new Procedure does not reserve this Mechanism.
     * getIdleRequest is especially in the latter case, because it can help to "clean up" after the
     * cancelled Procedure, returning this Mechanism back to some safe state.
     */
    protected R getIdleRequest() {
        return null;
    }

    /* package */ boolean isRunningPeriodic() {
        return isRunningPeriodic;
    }

    protected void checkContextReservation() {
        if (isRunningPeriodic()) {
            return;
        }
        if (superstructure != null) {
            if (!superstructure.isRunningPeriodic()) {
                var exception =
                        new IllegalStateException(
                                this.getName()
                                        + " is part of a superstructure but was used by something outside the superstructure");
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.ERROR,
                                exception.getMessage()
                                        + "\n"
                                        + StackTraceUtils.getStackTrace(exception.getStackTrace()));
                throw exception;
            }
            return;
        }
        ReservingCommand.checkCurrentCommandHasReservation(this);
    }

    @Override
    public final void periodic() {
        super.periodic();

        if (superstructure != null) {
            // This Mechanism's periodic() will be run by its superstructure.
            return;
        }

        periodicInternal();
    }

    /* package */ void periodicInternal() {
        try {
            isRunningPeriodic = true;
            if (request == null) {
                setRequest(getInitialRequest());
            }
            boolean wasRequestNew = isRequestNew;
            isRequestNew = false;
            run(request, wasRequestNew);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
        } finally {
            isRunningPeriodic = false;
        }
    }

    protected abstract void run(R request, boolean isRequestNew);
}
