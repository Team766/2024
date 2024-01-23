package com.team766.hal.wpilib;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;
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
    public int get() {
        StatusSignal<Double> positionSinceBoot = cancoder.getPositionSinceBoot();
        if (!positionSinceBoot.getStatus().isOK()) {
            Logger.get(Category.FRAMEWORK)
                    .logData(
                            Severity.ERROR,
                            "Unable to get position since boot: %s",
                            positionSinceBoot.getStatus().toString());
            return 0;
        }
        // yuck.
        return (int) Math.round(positionSinceBoot.getValue());
    }

    @Override
    public boolean getDirection() {
        CANcoderConfiguration configs = new CANcoderConfiguration();
        cancoder.getConfigurator().refresh(configs);
        // TODO: is this the direction boolean represents?  can't tell from doc or impl.
        return configs.MagnetSensor.SensorDirection == SensorDirectionValue.Clockwise_Positive;
    }

    @Override
    public double getDistance() {
        Logger.get(Category.FRAMEWORK)
                .logRaw(Severity.WARNING, "CANcoderEncoder.getDistance() not supported");
        return 0;
    }

    @Override
    public double getPosition() {
        StatusSignal<Double> position = cancoder.getPosition();
        if (!position.getStatus().isOK()) {
            Logger.get(Category.FRAMEWORK)
                    .logData(
                            Severity.ERROR,
                            "Unable to get position: %s",
                            position.getStatus().toString());
            return 0;
        }
        // yuck.
        return position.getValueAsDouble();
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
        // yuck.
        return velocity.getValueAsDouble();
    }

    @Override
    public boolean getStopped() {
        Logger.get(Category.FRAMEWORK)
                .logRaw(Severity.WARNING, "CANcoderEncoder.getStopped() not supported");
        return false;
    }

    @Override
    public void reset() {
        cancoder.setPosition(0);
    }

    @Override
    public void setDistancePerPulse(double distancePerPulse) {
        Logger.get(Category.FRAMEWORK)
                .logRaw(Severity.WARNING, "CANcoderEncoder.setDistancePerPulse() not supported");
        return;
    }
}
