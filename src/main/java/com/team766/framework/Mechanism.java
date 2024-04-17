package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Mechanism extends SubsystemBase implements LoggingBase {
    private ContextImpl<?> m_owningContext = null;
    private Thread m_runningPeriodic = null;

    protected Category loggerCategory = Category.MECHANISMS;

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    protected void checkContextOwnership() {
        if (ContextImpl.currentContext() != m_owningContext && m_runningPeriodic == null) {
            String message =
                    getName()
                            + " tried to be used by "
                            + ContextImpl.currentContext().getContextName();
            if (m_owningContext != null) {
                message += " while owned by " + m_owningContext.getContextName();
            } else {
                message += " without taking ownership of it";
            }
            Logger.get(Category.FRAMEWORK).logRaw(Severity.ERROR, message);
            throw new IllegalStateException(message);
        }
    }

    void takeOwnership(final ContextImpl<?> context, final ContextImpl<?> parentContext) {
        if (m_owningContext != null && m_owningContext == parentContext) {
            Logger.get(Category.FRAMEWORK)
                    .logRaw(
                            Severity.DEBUG,
                            context.getContextName()
                                    + " is inheriting ownership of "
                                    + getName()
                                    + " from "
                                    + parentContext.getContextName());
        } else {
            if (m_owningContext != context) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.DEBUG,
                                context.getContextName() + " is taking ownership of " + getName());
            }
            while (m_owningContext != null && m_owningContext != context) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.WARNING,
                                "Stopping previous owner of "
                                        + getName()
                                        + ": "
                                        + m_owningContext.getContextName());
                m_owningContext.cancel();
                var stoppedContext = m_owningContext;
                context.yield();
                if (m_owningContext == stoppedContext) {
                    Logger.get(Category.FRAMEWORK)
                            .logRaw(
                                    Severity.ERROR,
                                    "Previous owner of "
                                            + getName()
                                            + ", "
                                            + m_owningContext.getContextName()
                                            + " did not release ownership when requested. Release will be forced.");
                    m_owningContext.releaseOwnership(this);
                    break;
                }
            }
        }
        m_owningContext = context;
    }

    void releaseOwnership(final ContextImpl<?> context) {
        if (m_owningContext != context) {
            LoggerExceptionUtils.logException(
                    new Exception(
                            context.getContextName()
                                    + " tried to release ownership of "
                                    + getName()
                                    + " but it doesn't own it"));
            return;
        }
        Logger.get(Category.FRAMEWORK)
                .logRaw(
                        Severity.DEBUG,
                        context.getContextName() + " is releasing ownership of " + getName());
        m_owningContext = null;
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
