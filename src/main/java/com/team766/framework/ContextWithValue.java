package com.team766.framework;

/**
 * A {@link Context} that also allows the ProcedureWithValues running on it to yield values when
 * running asynchronously.
 */
public class ContextWithValue<T> extends Context implements LaunchedContextWithValue<T> {

    private T m_lastYieldedValue;

    @SuppressWarnings("unchecked")
    ContextWithValue(final RunnableWithContextWithValue<T> func, final Context parentContext) {
        super((context) -> func.run((ContextWithValue<T>) context), parentContext);
    }

    @SuppressWarnings("unchecked")
    ContextWithValue(final RunnableWithContextWithValue<T> func) {
        super((context) -> func.run((ContextWithValue<T>) context));
    }

    /**
     * Return the most recent value passed to yield(T).
     *
     * Implements LaunchedContextWithValue<T>
     */
    @Override
    public T lastYieldedValue() {
        return m_lastYieldedValue;
    }

    /**
     * Return the most recent value passed to yield(T), and clear the recorded last yielded value
     * such that subsequent calls to lastYieldedValue() will return null.
     *
     * Implements LaunchedContextWithValue<T>
     */
    @Override
    public T getAndClearLastYieldedValue() {
        final var result = m_lastYieldedValue;
        m_lastYieldedValue = null;
        return result;
    }

    /**
     * Momentarily pause execution of this Context to allow other Contexts to execute. Execution of
     * this Context will resume as soon as possible after the other Contexts have been given a
     * chance to run.
     *
     * The most recent value passed to this yield(T) method will be returned by subsequent calls to
     * lastYieldedValue().
     */
    public void yield(final T valueToYield) {
        m_lastYieldedValue = valueToYield;
        this.yield();
    }
}
