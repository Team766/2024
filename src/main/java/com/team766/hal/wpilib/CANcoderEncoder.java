package com.team766.hal.wpilib;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.hal.EncoderReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class CANcoderEncoder implements EncoderReader {

    private final CANcoder cancoder;
    private double distancePerPulse = 1;

    public CANcoderEncoder(int deviceId) {
        cancoder = new CANcoder(deviceId);
    }

    public CANcoderEncoder(int deviceId, String canBus) {
        cancoder = new CANcoder(deviceId, canBus);
    }

    @Override
    public boolean isConnected() {
        return cancoder.getPosition().getStatus().isOK();
    }

    @Override
    public double getDistance() {
        StatusSignal<Double> position = cancoder.getPosition();
        if (!position.getStatus().isOK()) {
            Logger.get(Category.HAL)
                    .logData(
                            Severity.ERROR,
                            "Unable to get position: %s",
                            position.getStatus().toString());
            return 0;
        }
        return distancePerPulse * position.getValueAsDouble();
    }

    @Override
    public double getRate() {
        StatusSignal<Double> velocity = cancoder.getVelocity();
        if (!velocity.getStatus().isOK()) {
            Logger.get(Category.HAL)
                    .logData(
                            Severity.ERROR,
                            "Unable to get rate: %s",
                            velocity.getStatus().toString());
            return 0;
        }
        return distancePerPulse * velocity.getValueAsDouble();
    }

    @Override
    public void reset() {
        cancoder.setPosition(0);
    }

    @Override
    public void setDistancePerPulse(double distancePerPulse) {
        // each "pulse" is a rotation
        this.distancePerPulse = distancePerPulse;
    }
}
