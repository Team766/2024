package com.team766.framework;

@FunctionalInterface
public interface RunnableWithContextWithValue<T> {
    void run(ContextWithValue<T> context);
}
