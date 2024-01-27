package com.team766.hal.wpilib;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.hal.EncoderReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

public class CANcoderEncoder implements EncoderReader {

    private final CANcoder cancoder;

    public CANcoderEncoder(int deviceId) {
        cancoder = new CANcoder(deviceId);
    }

    public CANcoderEncoder(int deviceId, String canBus) {
        cancoder = new CANcoder(deviceId, canBus);
    }

    @Override
    public double getDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRate() {
        StatusSignal<Double> velocity = cancoder.getVelocity();
        if (!velocity.getStatus().isOK()) {
            Logger.get(Category.FRAMEWORK)
                    .logData(
                            Severity.ERROR,
                            "Unable to get rate: %s",
                            velocity.getStatus().toString());
            return 0;
        }
        return velocity.getValueAsDouble();
    }

    @Override
    public void reset() {
        cancoder.setPosition(0);
    }

    @Override
    public void setDistancePerPulse(double distancePerPulse) {
        // TODO Auto-generated method stub

    }
}
