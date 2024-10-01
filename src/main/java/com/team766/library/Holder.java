package com.team766.library;

public class Holder<T> {
    public T value;

    public Holder() {}

    public Holder(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
