package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class RevThroughBoreDutyCycleEncoder extends DutyCycleEncoder implements EncoderReader {

    public RevThroughBoreDutyCycleEncoder(int channel) {
        super(channel);
        // TODO: figure out what this should be
        setDutyCycleRange(0, 4096);
    }

    @Override
    public double getRate() {
        throw new UnsupportedOperationException("getRate() not supported.");
    }

    @Override
    public void setDistancePerPulse(double distancePerPulse) {
        // each "pulse" is a rotation
        setDistancePerRotation(distancePerPulse);
    }
}
