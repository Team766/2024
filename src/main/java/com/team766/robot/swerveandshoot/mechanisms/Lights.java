package com.team766.robot.swerveandshoot.mechanisms;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.RainbowAnimation;
import com.team766.framework.Mechanism;

public class Lights extends Mechanism {

    private CANdle candle;
    private static final int CANID = 58; // config soon
    private static final int LED_COUNT = 53; // seems right
    private Animation rainbowAnimation = new RainbowAnimation(1, 1.5, LED_COUNT);

    public Lights() {
        candle = new CANdle(CANID);
    }

    public void purple() {
        checkContextOwnership();
        candle.setLEDs(128, 0, 128);
    }

    public void signalNoteInIntake() {
        checkContextOwnership();
        // NOTE: 255, 255, 255 trips the breaker. lol
        candle.setLEDs(128, 128, 128);
    }

    // public void signalNotInUse() {
    //     checkContextOwnership();
    //     candle.setLEDs(255, 150, 0);
    // }

    public void signalNoRing() {
        checkContextOwnership();
        candle.setLEDs(255, 0, 0);
    }

    public void turnLEDsOff() {
        checkContextOwnership();
        candle.setLEDs(0, 0, 0);
    }

    public void signalRing() {
        checkContextOwnership();
        candle.setLEDs(0, 255, 0);
    }

    public void orange() {
        checkContextOwnership();
        candle.setLEDs(255, 64, 0);
    }

    public void rainbow() {
        checkContextOwnership();
        candle.animate(rainbowAnimation);
    }
}
