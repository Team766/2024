package com.team766.framework;

import com.team766.logging.Category;
import edu.wpi.first.wpilibj2.command.Command;

/* package */ abstract class ProcedureBase extends Command implements LoggingBase {
    private static int c_idCounter = 0;

    private static synchronized int createNewId() {
        return c_idCounter++;
    }

    protected final int m_id;
    protected Category loggerCategory = Category.MECHANISMS;

    private ContextImpl<?> m_context = null;

    ProcedureBase() {
        m_id = createNewId();
        setName(this.getClass().getName() + "/" + m_id);
    }

    @Override
    public final Category getLoggerCategory() {
        return loggerCategory;
    }

    @Override
    public String toString() {
        return getName();
    }

    /* package */ abstract ContextImpl<?> makeContext();

    private Command command() {
        if (m_context == null) {
            m_context = makeContext();
        }
        return m_context;
    }

    @Override
    public void initialize() {
        command().initialize();
    }

    @Override
    public void execute() {
        command().execute();
    }

    @Override
    public void end(boolean interrupted) {
        command().end(interrupted);
    }

    @Override
    public boolean isFinished() {
        return command().isFinished();
    }

    @Override
    public boolean runsWhenDisabled() {
        return command().runsWhenDisabled();
    }
}
