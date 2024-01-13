package com.team766.hal.wpilib;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.Pigeon2;
import com.team766.hal.GyroReader;

public class PigeonGyro implements GyroReader {
    private final Pigeon2 pigeon;

    public PigeonGyro(int canId, String canBus) {
        pigeon = new Pigeon2(canId, canBus);
    }

    @Override
    public void calibrate() {
        pigeon.zeroGyroBiasNow();
    }

    @Override
    public void reset() {
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
        double[] xyz = new double[3];
        ErrorCode eCode = pigeon.getRawGyro(xyz);
        return eCode == ErrorCode.OK ? xyz[2] : 0;
    }
}
