package com.team766.framework;

public abstract class Procedure extends ProcedureBase implements RunnableWithContext {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends Procedure {
        @Override
        public void run(final Context context) {}
    }

    public static final Procedure NO_OP = new NoOpProcedure();
}
