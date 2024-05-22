package com.team766.hal.simulator;

import com.team766.framework.OIBase;
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
                motors[index] = new LocalMotorController(
                        configPrefix, new SimBasicMotorController(index), localSensor);
            } else {
                motors[index] = new SimMotorController(configPrefix, index);
            }
        }
        return motors[index];
    }

    @Override
    public EncoderReader getEncoder(final int index1, final int index2) {
        if (encoders[index1] == null) {
            encoders[index1] = new Encoder(index1);
        }
        return encoders[index1];
    }

    @Override
    public EncoderReader getEncoder(final int index1, String configPrefix) {
        if (encoders[index1] == null) {
            encoders[index1] = new Encoder(index1);
        }
        return encoders[index1];
    }

    @Override
    public SolenoidController getSolenoid(final int index) {
        if (solenoids[index] == null) {
            solenoids[index] = new Solenoid(index);
        }
        return solenoids[index];
    }

    @Override
    public GyroReader getGyro(final int index, String configPrefix) {
        if (gyros[0] == null) {
            gyros[0] = new Gyro();
        }
        return gyros[0];
    }

    @Override
    public CameraReader getCamera(final String id, final String value) {
        if (!cams.containsKey(id)) {
            cams.put(id, new Camera());
        }
        return cams.get(id);
    }

    @Override
    public JoystickReader getJoystick(OIBase oi, final int index) {
        return new Joystick(oi, index);
    }

    @Override
    public DigitalInputReader getDigitalInput(final int index) {
        if (digInputs[index] == null) {
            digInputs[index] = new DigitalInput(index);
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
            angInputs[index] = new AnalogInput(index);
        }
        return angInputs[index];
    }

    public RelayOutput getRelay(final int index) {
        if (relays[index] == null) {
            relays[index] = new Relay(index);
        }
        return relays[index];
    }

    @Override
    public PositionReader getPositionSensor() {
        if (positionSensor == null) {
            positionSensor = new PositionSensor();
        }
        return positionSensor;
    }

    @Override
    public BeaconReader getBeaconSensor() {
        if (beaconSensor == null) {
            beaconSensor = new BeaconSensor();
        }
        return beaconSensor;
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
