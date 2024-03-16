package com.team766.hal;

public class TestClock implements Clock {
    private double time;

    public TestClock(double time) {
        this.time = time;
    }

    public void tick(double seconds) {
        time += seconds;
    }

    public double getTime() {
        return time;
    }
}
