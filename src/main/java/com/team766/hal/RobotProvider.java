package com.team766.hal;

import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.controllers.TimeProviderI;
import com.team766.hal.mock.MockAnalogInput;
import com.team766.hal.mock.MockDigitalInput;
import com.team766.hal.mock.MockEncoder;
import com.team766.hal.mock.MockGyro;
import com.team766.hal.mock.MockMotorController;
import com.team766.hal.mock.MockRelay;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import java.util.Arrays;
import java.util.HashMap;

public abstract class RobotProvider {

    public static RobotProvider instance;

    private HashMap<Integer, String> motorDeviceIdNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> motorPortNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> digitalIoNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> analogInputNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> relayNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> solenoidNames = new HashMap<Integer, String>();
    private HashMap<Integer, String> gyroNames = new HashMap<Integer, String>();

    // HAL
    protected abstract MotorController getMotor(
            int index,
            String configPrefix,
            MotorController.Type type,
            ControlInputReader localSensor);

    public abstract EncoderReader getEncoder(int index1, int index2);

    public abstract EncoderReader getEncoder(int index, String configPrefix);

    public abstract DigitalInputReader getDigitalInput(int index);

    public abstract AnalogInputReader getAnalogInput(int index);

    public abstract RelayOutput getRelay(int index);

    public abstract SolenoidController getSolenoid(int index);

    public abstract GyroReader getGyro(int index, String configPrefix);

    public abstract CameraReader getCamera(String id, String value);

    public abstract PositionReader getPositionSensor();

    public abstract BeaconReader getBeaconSensor();

    public static TimeProviderI getTimeProvider() {
        return () -> instance.getClock().getTime();
    }

    // Config-driven methods

    private void checkDeviceName(
            final String deviceType,
            final HashMap<Integer, String> deviceNames,
            final Integer portId,
            final String configName) {
        String previousName = deviceNames.putIfAbsent(portId, configName);
        if (previousName != null && previousName != configName) {
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            "Multiple "
                                    + deviceType
                                    + " devices for port ID "
                                    + portId
                                    + ": "
                                    + previousName
                                    + ", "
                                    + configName);
        }
    }

    public MotorController getMotor(final String configName) {
        final String encoderConfigName = configName + ".encoder";
        final String analogInputConfigName = configName + ".analogInput";
        final ControlInputReader sensor =
                ConfigFileReader.getInstance().containsKey(encoderConfigName)
                        ? getEncoder(encoderConfigName)
                        : ConfigFileReader.getInstance().containsKey(analogInputConfigName)
                                ? getAnalogInput(analogInputConfigName)
                                : null;

        try {
            ValueProvider<Integer> deviceId =
                    ConfigFileReader.getInstance().getInt(configName + ".deviceId");
            final ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            final ValueProvider<Double> sensorScaleConfig =
                    ConfigFileReader.getInstance().getDouble(configName + ".sensorScale");
            final ValueProvider<Boolean> invertedConfig =
                    ConfigFileReader.getInstance().getBoolean(configName + ".inverted");
            final ValueProvider<Boolean> sensorInvertedConfig =
                    ConfigFileReader.getInstance().getBoolean(configName + ".sensorInverted");
            final ValueProvider<MotorController.Type> type =
                    ConfigFileReader.getInstance()
                            .getEnum(MotorController.Type.class, configName + ".type");

            if (deviceId.hasValue() && port.hasValue()) {
                Logger.get(Category.CONFIGURATION)
                        .logData(
                                Severity.ERROR,
                                "Motor %s configuration should have only one of `deviceId` or `port`",
                                configName);
            }

            MotorController.Type defaultType = MotorController.Type.TalonSRX;
            if (!deviceId.hasValue()) {
                deviceId = port;
                defaultType = MotorController.Type.VictorSP;
                checkDeviceName("PWM motor controller", motorPortNames, port.get(), configName);
            } else {
                checkDeviceName(
                        "CAN motor controller", motorDeviceIdNames, deviceId.get(), configName);
            }

            var motor = getMotor(deviceId.get(), configName, type.valueOr(defaultType), sensor);
            if (sensorScaleConfig.hasValue()) {
                motor = new MotorControllerWithSensorScale(motor, sensorScaleConfig.get());
            }
            if (invertedConfig.valueOr(false)) {
                motor.setInverted(true);
            }
            if (sensorInvertedConfig.valueOr(false)) {
                motor.setSensorInverted(true);
            }
            // check for, apply any PID settings that are in a sub-config
            configurePID(configName + ".pid.", motor);

            return motor;
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Error getting configuration for motor %s from config file, using mock motor instead.\nDetailed error: %s",
                            configName,
                            LoggerExceptionUtils.exceptionToString(ex));
            return new LocalMotorController(configName, new MockMotorController(0), sensor);
        }
    }

    private void configurePID(final String configName, MotorController motor) {
        ValueProvider<Double> pValue =
                ConfigFileReader.getInstance().getDouble(configName + PIDController.P_GAIN_KEY);
        ValueProvider<Double> iValue =
                ConfigFileReader.getInstance().getDouble(configName + PIDController.I_GAIN_KEY);
        ValueProvider<Double> dValue =
                ConfigFileReader.getInstance().getDouble(configName + PIDController.D_GAIN_KEY);
        ValueProvider<Double> ffValue =
                ConfigFileReader.getInstance().getDouble(configName + PIDController.FF_GAIN_KEY);
        ValueProvider<Double> outputMaxLowValue =
                ConfigFileReader.getInstance()
                        .getDouble(configName + PIDController.OUTPUT_MAX_LOW_KEY);
        ValueProvider<Double> outputMaxHighValue =
                ConfigFileReader.getInstance()
                        .getDouble(configName + PIDController.OUTPUT_MAX_HIGH_KEY);

        if (pValue.hasValue()) {
            motor.setP(pValue.get());
        }

        if (iValue.hasValue()) {
            motor.setI(iValue.get());
        }

        if (dValue.hasValue()) {
            motor.setD(dValue.get());
        }

        if (ffValue.hasValue()) {
            motor.setFF(ffValue.get());
        }

        if (outputMaxLowValue.hasValue() || outputMaxHighValue.hasValue()) {
            motor.setOutputRange(outputMaxLowValue.valueOr(-1.0), outputMaxHighValue.valueOr(1.0));
        }
    }

    public EncoderReader getEncoder(final String configName) {
        try {
            // check for an encoder on the CAN bus
            final ValueProvider<Integer> deviceId =
                    ConfigFileReader.getInstance().getInt(configName + ".deviceId");
            if (deviceId.hasValue()) {
                return getEncoder(deviceId.get(), configName);
            }
            // check for a single port encoder
            final ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            if (port.hasValue()) {
                // TODO: should we check the type here?
                return getEncoder(port.get(), configName);
            }
            // or a dual-port encoder
            final ValueProvider<Integer[]> ports =
                    ConfigFileReader.getInstance().getInts(configName + ".ports");
            final ValueProvider<Double> distancePerPulseConfig =
                    ConfigFileReader.getInstance().getDouble(configName + ".distancePerPulse");

            final var portsValue = ports.get();
            if (portsValue.length != 2) {
                Logger.get(Category.CONFIGURATION)
                        .logData(
                                Severity.ERROR,
                                "Encoder %s has %d config values, but expected 2",
                                configName,
                                portsValue.length);
                return new MockEncoder();
            }
            checkDeviceName("encoder/digital input", digitalIoNames, portsValue[0], configName);
            checkDeviceName("encoder/digital input", digitalIoNames, portsValue[1], configName);
            final EncoderReader reader = getEncoder(portsValue[0], portsValue[1]);
            if (distancePerPulseConfig.hasValue()) {
                reader.setDistancePerPulse(distancePerPulseConfig.get());
            }
            return reader;
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Encoder %s not found in config file, using mock encoder instead",
                            configName);
            return new MockEncoder();
        }
    }

    public DigitalInputReader getDigitalInput(final String configName) {
        try {
            ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            checkDeviceName("encoder/digital input", digitalIoNames, port.get(), configName);

            return getDigitalInput(port.get());
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Digital input %s not found in config file, using mock digital input instead",
                            configName);
            return new MockDigitalInput();
        }
    }

    public AnalogInputReader getAnalogInput(final String configName) {
        try {
            ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            checkDeviceName("analog input", analogInputNames, port.get(), configName);

            return getAnalogInput(port.get());
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Analog input %s not found in config file, using mock analog input instead",
                            configName);
            return new MockAnalogInput();
        }
    }

    public RelayOutput getRelay(final String configName) {
        try {
            ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            checkDeviceName("relay", relayNames, port.get(), configName);

            return getRelay(port.get());
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Relay %s not found in config file, using mock relay instead",
                            configName);
            return new MockRelay(0);
        }
    }

    public DoubleSolenoid getSolenoid(final String configName) {
        try {
            final String legacyConfigKey = configName + ".port";
            ValueProvider<Integer[]> forwardPorts =
                    ConfigFileReader.getInstance().containsKey(legacyConfigKey)
                            ? ConfigFileReader.getInstance().getInts(legacyConfigKey)
                            : ConfigFileReader.getInstance().getInts(configName + ".forwardPort");
            ValueProvider<Integer[]> reversePorts =
                    ConfigFileReader.getInstance().getInts(configName + ".reversePort");

            for (Integer port : forwardPorts.valueOr(new Integer[0])) {
                checkDeviceName("solenoid", solenoidNames, port, configName);
            }
            for (Integer port : reversePorts.valueOr(new Integer[0])) {
                checkDeviceName("solenoid", solenoidNames, port, configName);
            }

            SolenoidController forwardSolenoids =
                    new MultiSolenoid(
                            Arrays.stream(forwardPorts.valueOr(new Integer[0]))
                                    .<SolenoidController>map(this::getSolenoid)
                                    .toArray(SolenoidController[]::new));
            SolenoidController reverseSolenoids =
                    new MultiSolenoid(
                            Arrays.stream(reversePorts.valueOr(new Integer[0]))
                                    .<SolenoidController>map(this::getSolenoid)
                                    .toArray(SolenoidController[]::new));
            return new DoubleSolenoid(forwardSolenoids, reverseSolenoids);
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Solenoid %s not found in config file, using mock solenoid instead %s",
                            configName,
                            ex.toString());
            return new DoubleSolenoid(null, null);
        }
    }

    public GyroReader getGyro(final String configName) {
        try {
            ValueProvider<Integer> port =
                    ConfigFileReader.getInstance().getInt(configName + ".port");
            checkDeviceName("gyro", gyroNames, port.get(), configName);

            return getGyro(port.get(), configName);
        } catch (IllegalArgumentException ex) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Gyro %s not found in config file, using mock gyro instead",
                            configName);
            return new MockGyro();
        }
    }

    // Operator Devices
    public abstract JoystickReader getJoystick(int index);

    public abstract CameraInterface getCamServer();

    public abstract Clock getClock();

    public abstract double getBatteryVoltage();

    public abstract void refreshDriverStationData();

    public abstract boolean hasNewDriverStationData();
}
