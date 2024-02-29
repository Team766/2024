package com.team766.library;

import java.util.Optional;

public interface ValueProvider<E> extends Observable<Optional<E>> {
    E get();

    boolean hasValue();

    default E valueOr(E default_value) {
        if (hasValue()) {
            return get();
        }
        return default_value;
    }
}
