package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class ProcedureWithValue<T> extends ProcedureWithContextBase {
    protected ProcedureWithValue(Collection<Subsystem> reservations) {
        super(reservations);
    }

    private boolean isStatusActive = false;

    protected abstract void run(ContextWithValue<T> context);

    public final void execute(ContextWithValue<T> context) {
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
        return new ContextImpl<T>(this);
    }
}
