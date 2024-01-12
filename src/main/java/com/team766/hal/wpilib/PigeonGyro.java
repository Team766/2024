package com.team766.hal.wpilib;

import com.ctre.phoenix.sensors.Pigeon2;
import com.team766.hal.GyroReader;

public class PigeonGyro implements GyroReader {
    private final Pigeon2 pigeon;

    public PigeonGyro(int canId, String canBus) {
        pigeon = new Pigeon2(canId, canBus);
    }

    @Override
    public void calibrate() {
        // no-op.  the Pigeon2 is factory-calibrated.
    }

    @Override
    public void reset() {
        pigeon.zeroGyroBiasNow(); // is this worth calling?
        pigeon.setYaw(0);
    }

    @Override
    public double getAngle() {
        return pigeon.getYaw();
    }

    @Override
    public double getPitch() {
        return pigeon.getPitch();
    }

    @Override
    public double getRoll() {
        return pigeon.getRoll();
    }

    @Override
    public double getRate() {
        return 0; // not defined for Pigeon2.
    }
}
