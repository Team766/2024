package com.team766.hal;

/**
 * Clock implementation for usage in unit tests.
 */
public class TestClock implements Clock {
    private double time;

    /**
     * Create a new clock for the specified time, in seconds.
     * @param timeInSeconds time in seconds.
     */
    public TestClock(double timeInSeconds) {
        this.time = timeInSeconds;
    }

    /**
     * Advance the clock by the specified time, in seconds.
     * @param seconds how many seconds to advance.
     */
    public void tick(double seconds) {
        time += seconds;
    }

    @Override
    public double getTime() {
        return time;
    }
}
