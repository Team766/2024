package com.team766.framework;

/**
 * This interface can be used by the caller to manage Contexts created by startAsync.
 */
public interface LaunchedContext {
    /**
     * Returns a string meant to uniquely identify this Context (e.g. for use in
     * logging).
     */
    String getContextName();

    /**
     * Returns true if this Context has finished running, false otherwise.
     */
    boolean isDone();

    /**
     * Interrupt the running of this Context and force it to terminate.
     */
    void cancel();
}
