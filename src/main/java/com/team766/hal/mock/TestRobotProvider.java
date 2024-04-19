package com.team766.hal.mock;

import com.team766.hal.AnalogInputReader;
import com.team766.hal.BeaconReader;
import com.team766.hal.CameraInterface;
import com.team766.hal.CameraReader;
import com.team766.hal.Clock;
import com.team766.hal.ControlInputReader;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.EncoderReader;
import com.team766.hal.GyroReader;
import com.team766.hal.JoystickReader;
import com.team766.hal.LocalMotorController;
import com.team766.hal.MotorController;
import com.team766.hal.PositionReader;
import com.team766.hal.RelayOutput;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.wpilib.SystemClock;

public class TestRobotProvider extends RobotProvider {

    private MotorController[] motors = new MotorController[64];
    private boolean m_hasDriverStationUpdate = false;
    private double m_batteryVoltage = 12.0;

    @Override
    public MotorController getMotor(
            final int index,
            final String configPrefix,
            final MotorController.Type type,
            final ControlInputReader localSensor) {
        if (motors[index] == null) {
            motors[index] =
                    new LocalMotorController(
                            configPrefix,
                            new MockMotorController(index),
                            localSensor != null ? localSensor : new MockEncoder());
        }
        return motors[index];
    }

    @Override
    public EncoderReader getEncoder(final int index1, final int index2) {
        return new MockEncoder();
    }

    @Override
    public EncoderReader getEncoder(final int index1, String configPrefix) {
        return new MockEncoder();
    }

    @Override
    public SolenoidController getSolenoid(final int index) {
        return new MockSolenoid(index);
    }

    @Override
    public GyroReader getGyro(final int index, String configPrefix) {
        return new MockGyro();
    }

    @Override
    public CameraReader getCamera(final String id, final String value) {
        return new MockCamera();
    }

    @Override
    public JoystickReader getJoystick(final int index) {
        return new MockJoystick();
    }

    @Override
    public DigitalInputReader getDigitalInput(final int index) {
        return new MockDigitalInput();
    }

    @Override
    public CameraInterface getCamServer() {
        return null;
    }

    @Override
    public AnalogInputReader getAnalogInput(final int index) {
        return new MockAnalogInput();
    }

    public RelayOutput getRelay(final int index) {
        return new MockRelay(index);
    }

    @Override
    public PositionReader getPositionSensor() {
        return new MockPositionSensor();
    }

    @Override
    public BeaconReader getBeaconSensor() {
        return new MockBeaconSensor();
    }

    @Override
    public Clock getClock() {
        // TODO Replace this with a controlled clock
        return SystemClock.instance;
    }

    @Override
    public void refreshDriverStationData() {
        // no-op
    }

    @Override
    public boolean hasNewDriverStationData() {
        boolean result = m_hasDriverStationUpdate;
        m_hasDriverStationUpdate = false;
        return result;
    }

    public void setHasNewDriverStationData() {
        m_hasDriverStationUpdate = true;
    }

    @Override
    public double getBatteryVoltage() {
        return m_batteryVoltage;
    }

    public void setBatteryVoltage(final double voltage) {
        m_batteryVoltage = voltage;
    }
}
