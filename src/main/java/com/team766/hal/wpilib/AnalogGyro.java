package com.team766.hal.wpilib;

import com.team766.hal.GyroReader;

public class AnalogGyro extends edu.wpi.first.wpilibj.AnalogGyro implements GyroReader {
    private double offset = 0.0;

    public AnalogGyro(final int channel) {
        super(channel);
    }

    public double getPitch() {
        return 0.0;
    }

    public double getRoll() {
        return 0.0;
    }

    public void reset() {
        super.reset();
        offset = 0.0;
    }

    public void setAngle(double angle) {
        double currentAngle = getAngle();
        offset = angle - currentAngle;
    }

    public double getAngle() {
        return super.getAngle() + offset;
    }
}
