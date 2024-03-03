package com.team766.hal.mock;

import com.team766.hal.GyroReader;

public class MockGyro implements GyroReader {

    private double angle = 0;
    private double rate = 0;
    private double pitch = 0;
    private double roll = 0;

    public void calibrate() {
        reset();
    }

    public void reset() {
        angle = 0;
    }

    public double getAngle() {
        return angle;
    }

    public double getRate() {
        return rate;
    }

    public double getPitch() {
        return pitch;
    }

    public double getRoll() {
        return roll;
    }

    public void setAngle(final double angle_) {
        this.angle = angle_;
    }

    public void setRate(final double rate_) {
        this.rate = rate_;
    }

    public void setPitch(final double pitch_) {
        this.pitch = pitch_;
    }

    public void setRoll(final double roll_) {
        this.roll = roll_;
    }
}
