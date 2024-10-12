package com.team766.orin;

public class ValueNotFoundOnTableError extends Exception {
    public ValueNotFoundOnTableError(String key) {
        super("Value not found on table: " + key);
    }
}
