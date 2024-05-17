package com.team766.framework.conditions;

public class Guarded<T> {
    final T value;
    final RuleEngine engine;

    public Guarded(T value, RuleEngine engine) {
        this.value = value;
        this.engine = engine;
    }
}
