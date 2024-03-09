package com.team766.hal.wpilib;

import com.team766.hal.GyroReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj.SPI;

public class ADXRS450_Gyro extends edu.wpi.first.wpilibj.ADXRS450_Gyro implements GyroReader {
    private double offset = 0.0;

    public ADXRS450_Gyro(final SPI.Port port) {
        super(port);
        if (!isConnected()) {
            Logger.get(Category.HAL).logData(Severity.ERROR, "ADXRS450 Gyro is not connected!");
        } else {
            Logger.get(Category.HAL).logData(Severity.INFO, "ADXRS450 Gyro is connected");
        }
    }

    public double getPitch() {
        return 0.0;
    }

    public double getRoll() {
        return 0.0;
    }

    public void reset() {
        super.reset();
        offset = 0.0;
    }

    public void setAngle(double angle) {
        double currentAngle = super.getAngle();
        offset = angle - currentAngle;
    }

    public double getAngle() {
        return super.getAngle() + offset;
    }
}
