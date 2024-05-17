package com.team766.robot.swerveandshoot;

import com.ctre.phoenix.led.CANdle;
import com.team766.framework.LightsBase;
import com.team766.framework.Statuses;

public class Lights extends LightsBase {

    private CANdle candle;
    private static final int CANID = 58; // config soon

    public Lights() {
        candle = new CANdle(CANID);
    }

    @Override
    protected void dispatch(Statuses statuses) {
        // Add lights rules here
    }

    public void signalNoteInIntake() {
        // NOTE: 255, 255, 255 trips the breaker. lol
        candle.setLEDs(128, 128, 128);
    }

    public void signalNoRing() {
        candle.setLEDs(255, 0, 0);
    }

    public void turnLEDsOff() {
        candle.setLEDs(0, 0, 0);
    }

    public void signalRing() {
        candle.setLEDs(0, 255, 0);
    }
}
