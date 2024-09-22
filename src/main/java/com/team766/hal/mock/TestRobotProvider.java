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

public class TestRobotProvider extends RobotProvider {

    private final Clock clock;
    private MotorController[] motors = new MotorController[64];
    private boolean m_hasDriverStationUpdate = false;
    private double m_batteryVoltage = 12.0;

    public TestRobotProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public MotorController getMotor(
            final int index,
            final String configPrefix,
            final MotorController.Type type,
            final ControlInputReader localSensor) {
        if (motors[index] == null) {
            motors[index] = new LocalMotorController(
                    configPrefix,
                    new MockMotorController(index),
                    localSensor != null ? localSensor : new MockEncoder());
        }
        return motors[index];
    }

    @Override
    public EncoderReader getEncoder(final int index1, final int index2) {
        if (encoders[index1] == null) {
            encoders[index1] = new MockEncoder();
        }
        return encoders[index1];
    }

    @Override
    public EncoderReader getEncoder(final int index1, String configPrefix) {
        return new MockEncoder();
    }

    @Override
    public SolenoidController getSolenoid(final int index) {
        if (solenoids[index] == null) {
            solenoids[index] = new MockSolenoid(index);
        }
        return solenoids[index];
    }

    @Override
    public GyroReader getGyro(final int index, String configPrefix) {
        if (gyros[0] == null) {
            gyros[0] = new MockGyro();
        }
        return gyros[0];
    }

    @Override
    public CameraReader getCamera(final String id, final String value) {
        if (!cams.containsKey(id)) {
            cams.put(id, new MockCamera());
        }
        return cams.get(id);
    }

    @Override
    public JoystickReader getJoystick(final int index) {
        if (joysticks[index] == null) {
            joysticks[index] = new MockJoystick();
        }
        return joysticks[index];
    }

    @Override
    public DigitalInputReader getDigitalInput(final int index) {
        if (digInputs[index] == null) {
            digInputs[index] = new MockDigitalInput();
        }
        return digInputs[index];
    }

    @Override
    public CameraInterface getCamServer() {
        return null;
    }

    @Override
    public AnalogInputReader getAnalogInput(final int index) {
        if (angInputs[index] == null) {
            angInputs[index] = new MockAnalogInput();
        }
        return angInputs[index];
    }

    public RelayOutput getRelay(final int index) {
        if (relays[index] == null) {
            relays[index] = new MockRelay(index);
        }
        return relays[index];
    }

    @Override
    public PositionReader getPositionSensor() {
        if (positionSensor == null) {
            positionSensor = new MockPositionSensor();
        }
        return positionSensor;
    }

    @Override
    public BeaconReader getBeaconSensor() {
        if (beaconSensor == null) {
            beaconSensor = new MockBeaconSensor();
        }
        return beaconSensor;
    }

    @Override
    public Clock getClock() {
        return clock;
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
