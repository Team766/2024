package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

/* package */ abstract class ProcedureWithContextBase extends ProcedureBase {
    private ContextImpl<?> m_context = null;

    public ProcedureWithContextBase(Collection<Subsystem> reservations) {
        super(reservations);
    }

    /* package */ abstract ContextImpl<?> makeContext();

    private Command command() {
        if (m_context == null) {
            m_context = makeContext();
        }
        return m_context;
    }

    @Override
    public final void initialize() {
        command().initialize();
    }

    @Override
    public final void execute() {
        command().execute();
    }

    @Override
    public final void end(boolean interrupted) {
        command().end(interrupted);
    }

    @Override
    public final boolean isFinished() {
        return command().isFinished();
    }

    @Override
    public final boolean runsWhenDisabled() {
        return command().runsWhenDisabled();
    }
}
