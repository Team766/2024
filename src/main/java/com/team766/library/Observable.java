package com.team766.library;

/**
 * An Observable object can have one or more observers. An observer may be any object that
 * implements the Observer interface. When an Observable instance changes its value, it will notify
 * its observers of that change by a call to their onValueUpdated.
 */
public interface Observable<ValueType> {
    /**
     * Register an Observer to be notified of changes to this Observable object.
     */
    public void addObserver(Observer<ValueType> obs);

    /**
     * De-register an Observer that was previously passed to addObserver. This Observer will no
     * longer receive notifications about this Observable object.
     */
    public void removeObserver(Observer<ValueType> obs);
}
