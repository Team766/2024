package com.team766.hal.wpilib;

import com.team766.hal.EncoderReader;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

public class REVThroughBoreDutyCycleEncoder extends DutyCycleEncoder implements EncoderReader {

    public REVThroughBoreDutyCycleEncoder(int channel) {
        super(channel);
        setDutyCycleRange(1. / 1025., 1024. / 1025.);
        setConnectedFrequencyThreshold(976 /* 975.6 on spec sheet*/);
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
