package com.team766.hal.mock;

import com.team766.hal.PositionReader;

public class MockPositionSensor implements PositionReader {

    private double x = 0;
    private double y = 0;
    private double heading = 0;

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getHeading() {
        return heading;
    }

    public void setX(final double x_) {
        this.x = x_;
    }

    public void setY(final double y_) {
        this.y = y_;
    }

    public void setHeading(final double heading_) {
        this.heading = heading_;
    }
}
