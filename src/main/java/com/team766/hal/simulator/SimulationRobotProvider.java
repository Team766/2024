package com.team766.hal.simulator;

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
import com.team766.simulator.ProgramInterface;

public class SimulationRobotProvider extends RobotProvider {

    private MotorController[] motors = new MotorController[100];
    private int m_dsUpdateNumber = 0;

    @Override
    public MotorController getMotor(
            final int index,
            final String configPrefix,
            final MotorController.Type type,
            final ControlInputReader localSensor) {
        if (motors[index] == null) {
            if (localSensor != null) {
                motors[index] =
                        new LocalMotorController(
                                configPrefix, new SimBasicMotorController(index), localSensor);
            } else {
                motors[index] = new SimMotorController(configPrefix, index);
            }
        }
        return motors[index];
    }

    @Override
    public EncoderReader getEncoder(final int index1, final int index2) {
        return new Encoder(index1);
    }

    @Override
    public EncoderReader getEncoder(final int index1, String configPrefix) {
        return new Encoder(index1);
    }

    @Override
    public SolenoidController getSolenoid(final int index) {
        return new Solenoid(index);
    }

    @Override
    public GyroReader getGyro(final int index, String configPrefix) {
        return new Gyro();
    }

    @Override
    public CameraReader getCamera(final String id, final String value) {
        return new Camera();
    }

    @Override
    public JoystickReader getJoystick(final int index) {
        return ProgramInterface.joystickChannels[index];
    }

    @Override
    public DigitalInputReader getDigitalInput(final int index) {
        return new DigitalInput(index);
    }

    @Override
    public CameraInterface getCamServer() {
        return null;
    }

    @Override
    public AnalogInputReader getAnalogInput(final int index) {
        return new AnalogInput(index);
    }

    public RelayOutput getRelay(final int index) {
        return new Relay(index);
    }

    @Override
    public PositionReader getPositionSensor() {
        return new PositionSensor();
    }

    @Override
    public BeaconReader getBeaconSensor() {
        return new BeaconSensor();
    }

    @Override
    public Clock getClock() {
        return SimulationClock.instance;
    }

    @Override
    public void refreshDriverStationData() {
        // no-op on simulator
        return;
    }

    @Override
    public boolean hasNewDriverStationData() {
        int newUpdateNumber = ProgramInterface.driverStationUpdateNumber;
        boolean result = m_dsUpdateNumber != newUpdateNumber;
        m_dsUpdateNumber = newUpdateNumber;
        return result;
    }

    @Override
    public double getBatteryVoltage() {
        // TODO: The simulator currently doesn't include a simulation of electrical load.
        return 12.0;
    }
}
