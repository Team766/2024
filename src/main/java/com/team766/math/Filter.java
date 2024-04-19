package com.team766.math;

public interface Filter {
    void push(double sample);

    double getValue();
}
