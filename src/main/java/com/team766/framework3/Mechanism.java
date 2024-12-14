package com.team766.framework3;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.Objects;

public abstract class Mechanism<R extends Request<?>> extends SubsystemBase implements LoggingBase {
    private Thread m_runningPeriodic = null;

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
                    SchedulerMonitor.currentCommand = this;
                    setRequest(r);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LoggerExceptionUtils.logException(ex);
            } finally {
                SchedulerMonitor.currentCommand = null;
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

    protected void checkContextReservation() {
        var owningCommand = CommandScheduler.getInstance().requiring(this);
        if ((owningCommand == null || SchedulerMonitor.currentCommand != owningCommand)
                && m_runningPeriodic == null) {
            final String commandName =
                    SchedulerMonitor.currentCommand != null
                            ? SchedulerMonitor.currentCommand.getName()
                            : "non-Procedure code";
            String message = getName() + " tried to be used by " + commandName;
            if (owningCommand != null) {
                message += " while reserved by " + owningCommand.getName();
            } else {
                message += " without reserving it";
            }
            Logger.get(Category.FRAMEWORK).logRaw(Severity.ERROR, message);
            throw new IllegalStateException(message);
        }
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
            run(request, wasRequestNew);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
        } finally {
            m_runningPeriodic = null;
        }
    }

    protected abstract void run(R request, boolean isRequestNew);
}
