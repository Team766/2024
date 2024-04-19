package com.team766.library;

public interface ValueProvider<E> {
    E get();

    boolean hasValue();

    default E valueOr(E default_value) {
        if (hasValue()) {
            return get();
        }
        return default_value;
    }
}
