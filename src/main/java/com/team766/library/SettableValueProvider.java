package com.team766.library;

public interface SettableValueProvider<E> extends ValueProvider<E> {
    void set(E value);

    void clear();
}
