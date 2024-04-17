package com.team766.framework;

public abstract class ProcedureWithValue<T> extends ProcedureBase
        implements RunnableWithContextWithValue<T> {
    @Override
    /* package */ final ContextImpl<?> makeContext() {
        return new ContextImpl<T>(this);
    }
}
