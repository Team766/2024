package com.team766.hal.wpilib;

import com.team766.config.ConfigFileReader;
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
import com.team766.hal.mock.MockBeaconSensor;
import com.team766.hal.mock.MockEncoder;
import com.team766.hal.mock.MockGyro;
import com.team766.hal.mock.MockMotorController;
import com.team766.hal.mock.MockPositionSensor;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsControlModule;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SPI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WPIRobotProvider extends RobotProvider {

    /**
     * Runnable that counts the number of times we receive new data from the driver station. Used as
     * part of impl of {@link #hasNewDriverStationData()}.
     */
    private static class DataRefreshRunnable implements Runnable {
        private final AtomicBoolean m_keepAlive = new AtomicBoolean();
        private final AtomicInteger m_dataCount = new AtomicInteger();

        DataRefreshRunnable() {
            m_keepAlive.set(true);
        }

        public void cancel() {
            m_keepAlive.set(false);
        }

        @Override
        public void run() {
            // create and register a handle that gets notified whenever there's new DS data.
            int handle = WPIUtilJNI.createEvent(false, false);
            DriverStationJNI.provideNewDataEventHandle(handle);

            while (m_keepAlive.get()) {
                try {
                    // wait for new data or timeout
                    // (timeout returns true)
                    if (!WPIUtilJNI.waitForObjectTimeout(handle, 0.1)) {
                        m_dataCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // should only happen during failures
                    LoggerExceptionUtils.logException(e);

                    // clean up handle
                    DriverStationJNI.removeNewDataEventHandle(handle);
                    WPIUtilJNI.destroyEvent(handle);

                    // re-register handle in an attempt to keep data flowing.
                    handle = WPIUtilJNI.createEvent(false, false);
                    DriverStationJNI.provideNewDataEventHandle(handle);
                }
            }

            DriverStationJNI.removeNewDataEventHandle(handle);
            WPIUtilJNI.destroyEvent(handle);
        }
    }

    private DataRefreshRunnable m_DataRefreshRunnable = new DataRefreshRunnable();
    private Thread m_dataRefreshThread;
    private int m_lastDataCount = 0;

    public WPIRobotProvider() {
        m_dataRefreshThread = new Thread(m_DataRefreshRunnable, "DataRefreshThread");
        m_dataRefreshThread.start();
        try {
            ph.enableCompressorAnalog(90, 115);
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }

    private MotorController[][] motors =
            new MotorController[MotorController.Type.values().length][64];

    // The presence of this object allows the compressor to run before we've declared any solenoids.
    @SuppressWarnings("unused")
    private PneumaticsControlModule pcm = new PneumaticsControlModule();

    private PneumaticHub ph = new PneumaticHub();

    private static String getStringOrEmpty(ValueProvider<String> string) {
        return string.hasValue() ? string.get() : "";
    }

    @Override
    public MotorController getMotor(
            final int index,
            final String configPrefix,
            final MotorController.Type type,
            ControlInputReader localSensor) {
        if (motors[type.ordinal()][index] != null) {
            return motors[type.ordinal()][index];
        }
        MotorController motor = null;
        switch (type) {
            case SparkMax:
                try {
                    motor = new CANSparkMaxMotorController(index);
                } catch (Exception ex) {
                    LoggerExceptionUtils.logException(ex);
                    motor =
                            new LocalMotorController(
                                    configPrefix, new MockMotorController(index), localSensor);
                    localSensor = null;
                }
                break;
            case TalonSRX:
                motor = new CANTalonMotorController(index);
                break;
            case VictorSPX:
                motor = new CANVictorMotorController(index);
                break;
            case TalonFX:
                final ValueProvider<String> canBus =
                        ConfigFileReader.getInstance().getString(configPrefix + ".CANBus");
                motor = new CANTalonFxMotorController(index, getStringOrEmpty(canBus));
                break;
            case VictorSP:
                motor = new LocalMotorController(configPrefix, new PWMVictorSP(index), localSensor);
                localSensor = null;
                break;
            default:
                break;
        }
        if (motor == null) {
            LoggerExceptionUtils.logException(
                    new IllegalArgumentException("Unsupported motor type " + type));
            motor =
                    new LocalMotorController(
                            configPrefix, new MockMotorController(index), localSensor);
            localSensor = null;
        }
        if (localSensor != null) {
            motor = new LocalMotorController(configPrefix, motor, localSensor);
        }
        motors[type.ordinal()][index] = motor;
        return motor;
    }

    @Override
    public EncoderReader getEncoder(int index1, int index2) {
        if (encoders[index1] == null) {
            encoders[index1] = new Encoder(index1, index2);
        }
        return encoders[index1];
    }

    @Override
    public EncoderReader getEncoder(int index1, String configPrefix) {
        final ValueProvider<EncoderReader.Type> type =
                ConfigFileReader.getInstance()
                        .getEnum(EncoderReader.Type.class, configPrefix + ".type");

        if (type.hasValue()) {
            if (type.get() == EncoderReader.Type.CANcoder) {
                ValueProvider<String> canBus =
                        ConfigFileReader.getInstance().getString(configPrefix + ".CANBus");
                return new CANcoderEncoder(index1, canBus.get());
            } else if (type.get() == EncoderReader.Type.REVThroughBoreDutyCycle) {
                ValueProvider<Double> offset =
                        ConfigFileReader.getInstance().getDouble(configPrefix + ".offset");
                REVThroughBoreDutyCycleEncoder encoder = new REVThroughBoreDutyCycleEncoder(index1);
                if (offset.hasValue()) {
                    encoder.setPositionOffset(offset.get());
                }
                return encoder;
            } else {
                Logger.get(Category.CONFIGURATION)
                        .logData(
                                Severity.WARNING,
                                "Unknown encoder type: %s",
                                type.get().toString());
            }
        }
        return new MockEncoder();
    }

    @Override
    public SolenoidController getSolenoid(int index) {
        if (solenoids[index] == null) {
            solenoids[index] = new Solenoid(index);
        }
        return solenoids[index];
    }

    @Override
    // Gyro index values:
    // -1 = Spartan Gyro
    // 0+ = Analog Gyro on port index
    public GyroReader getGyro(final int index, String configPrefix) {

        final ValueProvider<GyroReader.Type> type =
                ConfigFileReader.getInstance()
                        .getEnum(GyroReader.Type.class, configPrefix + ".type");

        if (type.hasValue()) {
            if (type.get() == GyroReader.Type.Pigeon2) {
                ValueProvider<String> canBus =
                        ConfigFileReader.getInstance().getString(configPrefix + ".CANBus");
                return new PigeonGyro(index, getStringOrEmpty(canBus));
            }
        }

        if (gyros[index + 2] == null) {
            if (index < -2) {
                Logger.get(Category.CONFIGURATION)
                        .logRaw(
                                Severity.ERROR,
                                "Invalid gyro port "
                                        + index
                                        + ". Must be -2, -1, or a non-negative integer");
                return new MockGyro();
            } else if (index == -2) {
                gyros[index + 2] = new NavXGyro(I2C.Port.kOnboard);
            } else if (index == -1) {
                gyros[index + 2] = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
            } else {
                gyros[index + 2] = new AnalogGyro(index);
            }
        }
        return gyros[index + 2];
    }

    @Override
    public CameraReader getCamera(final String id, final String value) {
        System.err.println("Camera support not yet avaible");
        return null;
    }

    @Override
    public JoystickReader getJoystick(int index) {
        if (joysticks[index] == null) {
            joysticks[index] = new Joystick(index);
        }
        return joysticks[index];
    }

    @Override
    public CameraInterface getCamServer() {
        return new com.team766.hal.wpilib.CameraInterface();
    }

    @Override
    public DigitalInputReader getDigitalInput(final int index) {
        if (digInputs[index] == null) {
            digInputs[index] = new DigitalInput(index);
        }
        return digInputs[index];
    }

    @Override
    public AnalogInputReader getAnalogInput(int index) {
        if (angInputs[index] == null) {
            angInputs[index] = new AnalogInput(index);
        }
        return angInputs[index];
    }

    @Override
    public RelayOutput getRelay(int index) {
        if (relays[index] == null) {
            relays[index] = new Relay(index);
        }
        return relays[index];
    }

    @Override
    public PositionReader getPositionSensor() {
        if (positionSensor == null) {
            positionSensor = new MockPositionSensor();
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            "Position sensor does not exist on real robots. Using mock position sensor instead - it will always return a position of 0");
        }
        return positionSensor;
    }

    @Override
    public BeaconReader getBeaconSensor() {
        if (beaconSensor == null) {
            beaconSensor = new MockBeaconSensor();
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            "Beacon sensor does not exist on real robots. Using mock beacon sensor instead - it will always return no beacons");
        }
        return beaconSensor;
    }

    @Override
    public Clock getClock() {
        return SystemClock.instance;
    }

    @Override
    public void refreshDriverStationData() {
        DriverStation.refreshData();
    }

    @Override
    public boolean hasNewDriverStationData() {
        // see if the thread has counted more data changes than the last time this method was called
        int currentDataRefreshThreadCount = m_DataRefreshRunnable.m_dataCount.get();
        boolean dataChanged = m_lastDataCount != currentDataRefreshThreadCount;
        m_lastDataCount = currentDataRefreshThreadCount;
        return dataChanged;
    }

    @Override
    public double getBatteryVoltage() {
        return RobotController.getBatteryVoltage();
    }
}
