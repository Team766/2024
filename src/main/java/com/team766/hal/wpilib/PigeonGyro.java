package com.team766.hal.wpilib;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.team766.hal.GyroReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO: add support for configuring the Pigeon2's mountpose
// https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/core/CorePigeon2.html
// TODO: add support for getting the robot's heading as a Rotation2d, etc.
// https://api.ctr-electronics.com/phoenix6/release/java/com/ctre/phoenix6/hardware/Pigeon2.html
public class PigeonGyro implements GyroReader {
    private final Pigeon2 pigeon;

    public PigeonGyro(int canId, String canBus) {
        pigeon = new Pigeon2(canId, canBus);
    }

    @Override
    public void calibrate() {}

    @Override
    public void reset() {
        pigeon.setYaw(0);
    }

    @Override
    public double getAngle() {
        // Negative since getAngle returns CW+ and our standard is CCW        return -pigeon.getAngle();
    }

    @Override
    public double getPitch() {
        StatusSignal<Double> value = pigeon.getPitch();
        if (value.getStatus().isOK()) {
            return value.getValueAsDouble();
        }
        Logger.get(Category.GYRO)
                .logData(Severity.ERROR, "Unable to get pitch: %s", value.toString());
        return 0;
    }

    @Override
    public double getRoll() {
        StatusSignal<Double> value = pigeon.getRoll();
        if (value.getStatus().isOK()) {
            return value.getValueAsDouble();
        }
        Logger.get(Category.GYRO)
                .logData(Severity.ERROR, "Unable to get roll: %s", value.toString());
        return 0;
    }

    @Override
    public double getRate() {
        return pigeon.getRate();
    }
}
