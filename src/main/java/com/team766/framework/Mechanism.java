package com.team766.framework;

import com.team766.framework.resources.Reservable;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Mechanism extends SubsystemBase implements Reservable, LoggingBase {
    private Thread m_runningPeriodic = null;

    protected Category loggerCategory = Category.MECHANISMS;

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    protected void checkContextOwnership() {
        if (SchedulerMonitor.getCurrentCommand() != this.getCurrentCommand()
                && m_runningPeriodic == null) {
            String message =
                    getName()
                            + " tried to be used by "
                            + SchedulerMonitor.getCurrentCommand().getName();
            if (this.getCurrentCommand() != null) {
                message += " while owned by " + this.getCurrentCommand().getName();
            } else {
                message += " without requiring it";
            }
            message += "\n" + StackTraceUtils.getStackTrace(Thread.currentThread());
            Logger.get(Category.FRAMEWORK).logRaw(Severity.ERROR, message);
            throw new IllegalStateException(message);
        }
    }

    @Override
    public final void periodic() {
        try {
            m_runningPeriodic = Thread.currentThread();
            run();
        } finally {
            m_runningPeriodic = null;
        }
    }

    public void run() {}
}
