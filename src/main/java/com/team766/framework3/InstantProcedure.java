package com.team766.framework3;

public abstract non-sealed class InstantProcedure extends ProcedureBase implements InstantRunnable {
    @Override
    public final void run(Context context) {
        run();
    }
}
