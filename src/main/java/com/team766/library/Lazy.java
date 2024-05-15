package com.team766.library;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T value;
    private boolean initialized;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public T get() {
        if (!initialized) {
            value = supplier.get();
            initialized = true;
        }
        return value;
    }
}
