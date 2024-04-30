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
        public void run(final Context context) {}
    }

    public static Procedure noOp() {
        return new NoOpProcedure();
    }

    public Procedure(Collection<Subsystem> reservations) {
        super(reservations);
    }

    @Override
    /* package */ final ContextImpl<?> makeContext() {
        return new ContextImpl<>(this);
    }
}
