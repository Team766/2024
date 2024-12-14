package com.team766.hal;

import com.team766.hal.wpilib.SystemClock;

/**
 * Clock implementation for usage in unit tests.
 */
public class TestClock implements Clock {
    private boolean useSystemClock = true;
    private double time;

    /**
     * Create a new clock that returns the system time, until one of the methods to control the
     * clock time are called. After that, the clock will return the same time until its time is
     * changed again.
     */
    public TestClock() {
        useSystemClock = true;
    }

    /**
     * Create a new clock set to the specified time. The clock will return the same time until its
     * time is changed again.
     */
    public TestClock(double time) {
        useSystemClock = false;
        this.time = time;
    }

    /**
     * Set the clock's current time.
     * After calling this, the clock will no longer advance automatically.
     */
    public void setTime(double time) {
        this.useSystemClock = false;
        this.time = time;
    }

    /**
     * Advance the clock by the specified time, in seconds.
     * @param seconds how many seconds to advance.
     */
    public void tick(double seconds) {
        if (useSystemClock) {
            setTime(SystemClock.instance.getTime());
        }
        time += seconds;
    }

    @Override
    public double getTime() {
        if (useSystemClock) {
            return SystemClock.instance.getTime();
        } else {
            return time;
        }
    }
}
