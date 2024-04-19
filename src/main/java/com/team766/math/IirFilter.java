package com.team766.math;

public class IirFilter implements Filter {
    private double decay;
    private double value;

    public IirFilter(final double decay_, final double initialValue_) {
        this.decay = decay_;
        this.value = initialValue_;
        if (decay > 1.0 || decay <= 0.0) {
            throw new IllegalArgumentException("decay should be in (0.0, 1.0]");
        }
    }

    public IirFilter(final double decay_) {
        this(decay_, 0.0);
    }

    public void push(final double sample) {
        value *= (1.0 - decay);
        value += sample * decay;
    }

    public double getValue() {
        return value;
    }
}
