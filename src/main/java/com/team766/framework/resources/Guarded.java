package com.team766.framework.resources;

public class Guarded<T> {
    final T value;
    final ResourceManager manager;

    public Guarded(T value, ResourceManager manager) {
        this.value = value;
        this.manager = manager;
    }
}
