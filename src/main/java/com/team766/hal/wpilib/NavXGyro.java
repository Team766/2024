package com.team766.hal.wpilib;

import com.kauailabs.navx.frc.AHRS;
import com.team766.hal.GyroReader;
import edu.wpi.first.wpilibj.I2C;

public class NavXGyro implements GyroReader {
    private final AHRS m_gyro;
    private double offset = 0.0;

    public NavXGyro(final I2C.Port port) {
        m_gyro = new AHRS(port);
        // NOTE: It takes a bit of time until the gyro reader thread updates
        // the connected status, so we can't check it immediately.
        // TODO: Replace this with a status indicator
        /*if (!m_gyro.isConnected()) {
        	Logger.get(Category.HAL).logData(Severity.ERROR, "NavX Gyro is not connected!");
        } else {
        	Logger.get(Category.HAL).logData(Severity.INFO, "NavX Gyro is connected");
        }*/
    }

    @Override
    public void calibrate() {
        // m_gyro.calibrate(); calibrate() seems to have been removed.
        // it may have been a no-op anyway?
        // https://github.com/kauailabs/navxmxp/blob/master/roborio/java/navx_frc/src/com/kauailabs/navx/frc/AHRS.java
    }

    @Override
    public void reset() {
        m_gyro.reset();
        offset = 0.0;
    }

    public void setAngle(double angle) {
        double currentAngle = m_gyro.getAngle();
        offset = angle - currentAngle;
    }

    @Override
    public double getAngle() {
        return m_gyro.getAngle() + offset;
    }

    @Override
    public double getRate() {
        return m_gyro.getRate();
    }

    @Override
    public double getPitch() {
        return m_gyro.getPitch();
    }

    @Override
    public double getRoll() {
        return m_gyro.getRoll();
    }
}
