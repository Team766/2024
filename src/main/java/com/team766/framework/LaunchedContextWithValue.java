package com.team766.framework;

/**
 * This interface can be used by the caller to manage Contexts created by startAsync which are
 * expected to yield values.
 */
public interface LaunchedContextWithValue<T> extends LaunchedContext {
    /**
     * Return the most recent value that the Procedure passed to Context.yield(T).
     * Return null if a value has never been yielded, or a value has not been yielded since the last
     * call to getAndClearLastYieldedValue.
     */
    T lastYieldedValue();

    /**
     * Return the most recent value that the Procedure passed to Context.yield(T), and clear the
     * recorded last yielded value such that subsequent calls to lastYieldedValue() will return
     * null.
     */
    T getAndClearLastYieldedValue();
}
