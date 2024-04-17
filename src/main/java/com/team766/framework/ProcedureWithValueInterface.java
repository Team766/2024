package com.team766.framework;

@FunctionalInterface
public interface ProcedureWithValueInterface<T> {
    void run(ContextWithValue<T> context);
}
