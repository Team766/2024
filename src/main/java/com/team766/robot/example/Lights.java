package com.team766.robot.example;

import com.ctre.phoenix.led.CANdle;
import com.team766.framework3.RuleEngine;

public class Lights extends RuleEngine {
    private static final int CANID = 5;
    private final CANdle candle = new CANdle(CANID);

    public Lights() {
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
