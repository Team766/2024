package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class Procedure extends ProcedureWithContextBase implements ProcedureInterface {
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

    @Override
    /* package */ final ContextImpl<?> makeContext() {
        return new ContextImpl<>(this);
    }
}
