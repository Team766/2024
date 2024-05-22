package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class Procedure extends ProcedureBase implements ProcedureInterface {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends Procedure {
        public NoOpProcedure() {
            super(NO_RESERVATIONS);
        }

        @Override
        protected void run(final Context context) {}
    }

    public static Procedure noOp() {
        return new NoOpProcedure();
    }

    private ContextImpl m_context = null;

    private boolean isStatusActive = false;

    public Procedure(Collection<Subsystem> reservations) {
        super(reservations);
    }

    protected abstract void run(Context context);

    @Override
    public final void execute(Context context) {
        try {
            isStatusActive = true;
            run(context);
        } finally {
            isStatusActive = false;
        }
    }

    @Override
    public final boolean isStatusActive() {
        return isStatusActive;
    }

    private Command command() {
        if (m_context == null) {
            m_context = new ContextImpl(this);
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
