package com.team766.framework;

/**
 * A {@link Context} that also allows the ProcedureWithValues running on it to yield values when
 * running asynchronously.
 */
public interface ContextWithValue<T> extends Context {
    /**
     * Momentarily pause execution of this Context to allow other Contexts to execute. Execution of
     * this Context will resume as soon as possible after the other Contexts have been given a
     * chance to run.
     *
     * The most recent value passed to this yield(T) method will be returned by subsequent calls to
     * LaunchedContextWithValue.lastYieldedValue().
     */
    public void yield(final T valueToYield);
}
