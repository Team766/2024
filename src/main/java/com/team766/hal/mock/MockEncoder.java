package com.team766.hal.mock;

import com.team766.hal.EncoderReader;

public class MockEncoder implements EncoderReader {

    private double distance = 0;
    private double rate = 0;
    private double distancePerPulse = 1;

    public MockEncoder() {}

    @Override
    public void reset() {
        distance = 0;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    @Override
    public double getRate() {
        return this.rate;
    }

    public void setDistance(final double distance_) {
        this.distance = distance_;
    }

    public void setRate(final double rate_) {
        this.rate = rate_;
    }

    @Override
    public void setDistancePerPulse(final double distancePerPulse_) {
        this.distancePerPulse = distancePerPulse_;
    }
}
