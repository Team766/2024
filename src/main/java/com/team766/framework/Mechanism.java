package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Mechanism extends SubsystemBase implements LoggingBase {
    private Thread m_runningPeriodic = null;

    protected Category loggerCategory = Category.MECHANISMS;

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    protected void checkContextOwnership() {
        if (ContextImpl.currentContext() != getCurrentCommand() && m_runningPeriodic == null) {
            String message =
                    getName()
                            + " tried to be used by "
                            + ContextImpl.currentContext().getContextName();
            if (getCurrentCommand() != null) {
                message += " while owned by " + getCurrentCommand().getName();
            } else {
                message += " without taking ownership of it";
            }
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
