package com.team766.framework;

public abstract class Procedure extends ProcedureBase implements ProcedureInterface {
    // A reusable Procedure that does nothing.
    private static final class NoOpProcedure extends Procedure {
        @Override
        public void run(final Context context) {}
    }

    public static Procedure noOp() {
        return new NoOpProcedure();
    }

    @Override
    /* package */ final ContextImpl<?> makeContext() {
        return new ContextImpl<>(this);
    }
}
