package com.team766.framework3;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Mechanism<R extends Request<?>> extends SubsystemBase implements LoggingBase {
    private Thread m_runningPeriodic = null;

    private R request = null;
    private boolean isRequestNew = false;

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
            boolean wasRequestNew = isRequestNew;
            isRequestNew = false;
            run(request, wasRequestNew);
        } finally {
            m_runningPeriodic = null;
        }
    }

    protected abstract void run(R request, boolean isRequestNew);
}
