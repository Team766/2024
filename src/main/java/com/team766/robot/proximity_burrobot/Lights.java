package com.team766.robot.proximity_burrobot;

import com.ctre.phoenix.led.CANdle;
import com.team766.framework.LightsBase;
import com.team766.framework.Statuses;

public class Lights extends LightsBase {
    private final CANdle candle;
    private static final int CANID = 5;

    public Lights() {
        candle = new CANdle(CANID);
    }

    @Override
    protected void dispatch(Statuses statuses) {
        // Add lights rules here
    }

    public void purple() {
        candle.setLEDs(128, 0, 128);
    }

    public void white() {
        // NOTE: 255, 255, 255 trips the breaker. lol
        candle.setLEDs(128, 128, 128);
    }

    public void yellow() {
        candle.setLEDs(255, 150, 0);
    }

    public void red() {
        candle.setLEDs(255, 0, 0);
    }

    public void green() {
        candle.setLEDs(0, 255, 0);
    }

    public void orange() {
        candle.setLEDs(255, 64, 0);
    }
}
