package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class DutyCycleEncoderReader extends DutyCycleEncoder implements EncoderReader {

    public DutyCycleEncoderReader(int channel) {
        super(channel);
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
