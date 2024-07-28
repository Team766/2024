package com.team766.framework.resources;

public class Guarded<T> {
    private final T obj;

    public static <T> Guarded<T> guard(T obj) {
        return new Guarded<>(obj);
    }

    public Guarded(T obj) {
        this.obj = obj;
    }

    /*package*/ T get() {
        return obj;
    }
}
