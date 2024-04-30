package com.team766.framework;

import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class ProcedureWithValue<T> extends ProcedureWithContextBase {
    protected ProcedureWithValue(Collection<Subsystem> reservations) {
        super(reservations);
    }

    public abstract void run(ContextWithValue<T> context);

    @Override
    /* package */ final ContextImpl<?> makeContext() {
        return new ContextImpl<T>(this);
    }
}
