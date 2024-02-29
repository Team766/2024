package com.team766.library;

@FunctionalInterface
public interface Observer<ValueType> {
    public void onValueUpdated(ValueType value);
}
