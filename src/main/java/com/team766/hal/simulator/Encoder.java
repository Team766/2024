package com.team766.hal.simulator;

import com.team766.hal.EncoderReader;
import com.team766.simulator.ProgramInterface;

public class Encoder implements EncoderReader {

    private final int channel;
    private double distancePerPulse = 1.0;

    public Encoder(final int channel) {
        this.channel = channel;
    }

    @Override
    public void reset() {
        set(0);
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public double getDistance() {
        int distance = (int) ProgramInterface.encoderChannels[channel].distance;
        return distance * distancePerPulse;
    }

    @Override
    public double getRate() {
        return ProgramInterface.encoderChannels[channel].rate * distancePerPulse;
    }

    @Override
    public void setDistancePerPulse(final double distancePerPulse_) {
        this.distancePerPulse = distancePerPulse_;
    }

    public void set(final int tick) {
        ProgramInterface.encoderChannels[channel].distance = tick;
    }
}
