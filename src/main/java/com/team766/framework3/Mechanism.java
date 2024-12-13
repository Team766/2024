package com.team766.framework3;

import com.team766.framework.StackTraceUtils;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.NoSuchElementException;

public abstract class Mechanism<R extends Request, S extends Record & Status> extends SubsystemBase
        implements LoggingBase {
    private Thread m_runningPeriodic = null;

    Mechanism<?, ?> superstructure = null;

    private R request = null;
    private boolean isRequestNew = false;
    private S status = null;

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

    public final void setRequest(R request) {
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

    protected void checkContextReservation() {
        if (m_runningPeriodic != null) {
            return;
        }
        if (superstructure != null) {
            if (superstructure.m_runningPeriodic == null) {
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

    public S getMechanismStatus() {
        if (status == null) {
            throw new NoSuchElementException(getName() + " has not published a status yet");
        }
        return status;
    }

    @Override
    public final void periodic() {
        super.periodic();

        try {
            m_runningPeriodic = Thread.currentThread();
            if (request == null) {
                setRequest(getInitialRequest());
            }
            boolean wasRequestNew = isRequestNew;
            isRequestNew = false;
            status = run(request, wasRequestNew);
            StatusBus.publishStatus(status);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
        } finally {
            m_runningPeriodic = null;
        }
    }

    protected abstract S run(R request, boolean isRequestNew);
}
