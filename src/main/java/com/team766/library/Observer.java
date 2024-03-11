package com.team766.library;

/**
 * A class can implement the Observer interface when it wants to be informed of changes to
 * Observable objects.
 */
@FunctionalInterface
public interface Observer<ValueType> {
    /**
     * This method is called whenever the observed object is changed.
     *
     * @param value The new value of the Observable object.
     */
    public void onValueUpdated(ValueType value);
}
