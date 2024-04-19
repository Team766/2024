package com.team766.math;

public interface Algebraic<E extends Algebraic<E>> {
    E add(E b);

    E scale(double b);
}
