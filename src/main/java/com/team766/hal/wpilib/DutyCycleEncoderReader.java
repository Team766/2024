package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class DutyCycleEncoderReader extends DutyCycleEncoder implements EncoderReader {

    public DutyCycleEncoderReader(int channel) {
        super(channel);
    }

    @Override
    public double getRate() {
        // getRate() is not supported in the current WPILib DutyCycleEncoder class.
        // DutyCycleEncoder does include a Counter, which does support getRate(), so we might be
        // able to add support via a small change in WPILib.
        throw new UnsupportedOperationException("getRate() not supported.");
    }

    @Override
    public void setDistancePerPulse(double distancePerPulse) {
        // each "pulse" is a rotation
        setDistancePerRotation(distancePerPulse);
    }
}
