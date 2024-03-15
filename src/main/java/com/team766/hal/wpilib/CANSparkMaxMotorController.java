package com.team766.hal.wpilib;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkAnalogSensor;
import com.team766.hal.MotorController;
import com.team766.hal.MotorControllerCommandFailedException;
import com.team766.hal.PIDSlotHelper;
import com.team766.library.ValueProvider;
import com.team766.logging.LoggerExceptionUtils;
import java.util.function.Function;
import java.util.function.Supplier;

public class CANSparkMaxMotorController extends CANSparkMax
        implements MotorController, PIDSlotHelper.MotorCallbacks {

    private static final int NUM_PID_SLOTS = 2; // should be 4, only exposing 2 at this time.

    private Supplier<Double> sensorPositionSupplier;
    private Supplier<Double> sensorVelocitySupplier;
    private Function<Double, REVLibError> sensorPositionSetter;
    private Function<Boolean, REVLibError> sensorInvertedSetter;
    private boolean sensorInverted = false;
    private final PIDSlotHelper pidSlotHelper;

    public CANSparkMaxMotorController(final int deviceId) {
        super(deviceId, MotorType.kBrushless);

        // Set default feedback device. This ensures that our implementations of
        // getSensorPosition/getSensorVelocity return values that match what the
        // device's PID controller is using.
        setSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        pidSlotHelper = new PIDSlotHelper(this);
    }

    private enum ExceptionTarget {
        THROW,
        LOG,
    }

    private static void revErrorToException(final ExceptionTarget throwEx, final REVLibError err) {
        if (err == REVLibError.kOk) {
            return;
        }
        var ex = new MotorControllerCommandFailedException(err.toString());
        switch (throwEx) {
            case THROW:
                throw ex;
            default:
            case LOG:
                LoggerExceptionUtils.logException(ex);
                break;
        }
    }

    @Override
    public double getSensorPosition() {
        return sensorPositionSupplier.get();
    }

    @Override
    public double getSensorVelocity() {
        return sensorVelocitySupplier.get();
    }

    @Override
    public void set(
            final ControlMode mode, final double value, int slot, double arbitraryFeedForward) {
        switch (mode) {
            case Disabled:
                disable();
                break;
            case PercentOutput:
                getPIDController()
                        .setReference(
                                value,
                                CANSparkMax.ControlType.kDutyCycle,
                                slot,
                                arbitraryFeedForward);
                break;
            case Position:
                getPIDController()
                        .setReference(
                                value,
                                CANSparkMax.ControlType.kPosition,
                                slot,
                                arbitraryFeedForward);
                break;
            case Velocity:
                getPIDController()
                        .setReference(
                                value,
                                CANSparkMax.ControlType.kVelocity,
                                slot,
                                arbitraryFeedForward);
                break;
            case Voltage:
                getPIDController()
                        .setReference(
                                value,
                                CANSparkMax.ControlType.kVoltage,
                                slot,
                                arbitraryFeedForward);
            default:
                throw new IllegalArgumentException("Unsupported control mode " + mode);
        }
    }

    @Override
    public void setSensorPosition(final double position) {
        revErrorToException(ExceptionTarget.THROW, sensorPositionSetter.apply(position));
    }

    @Override
    public void follow(final MotorController leader) {
        // see if the follow request should specify inverting the motor.
        // we do this based on whether or not the leader and follower have different inversion
        // settings.
        boolean invert = getInverted() != leader.getInverted();
        try {
            revErrorToException(ExceptionTarget.LOG, super.follow((CANSparkMax) leader, invert));
        } catch (ClassCastException ex) {
            LoggerExceptionUtils.logException(
                    new IllegalArgumentException(
                            "Spark Max can only follow another Spark Max", ex));
        }
    }

    @Override
    public void setNeutralMode(final NeutralMode neutralMode) {
        switch (neutralMode) {
            case Brake:
                revErrorToException(ExceptionTarget.LOG, setIdleMode(IdleMode.kBrake));
                break;
            case Coast:
                revErrorToException(ExceptionTarget.LOG, setIdleMode(IdleMode.kCoast));
                break;
            default:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("Unsupported neutral mode " + neutralMode));
                break;
        }
    }

    @Override
    public int numPIDSlots() {
        return NUM_PID_SLOTS;
    }

    @Override
    public void setP(ValueProvider<Double> value, int slot) {
        pidSlotHelper.setP(value, slot);
    }

    @Override
    public void setP_Impl(final double value, int slot) {
        revErrorToException(ExceptionTarget.LOG, getPIDController().setP(value, slot));
    }

    @Override
    public void setI(ValueProvider<Double> value, int slot) {
        pidSlotHelper.setI(value, slot);
    }

    @Override
    public void setI_Impl(final double value, int slot) {
        revErrorToException(ExceptionTarget.LOG, getPIDController().setI(value, slot));
    }

    @Override
    public void setD(ValueProvider<Double> value, int slot) {
        pidSlotHelper.setD(value, slot);
    }

    @Override
    public void setD_Impl(final double value, int slot) {
        revErrorToException(ExceptionTarget.LOG, getPIDController().setD(value, slot));
    }

    @Override
    public void setFF(ValueProvider<Double> value, int slot) {
        pidSlotHelper.setFF(value, slot);
    }

    @Override
    public void setFF_Impl(final double value, int slot) {
        revErrorToException(ExceptionTarget.LOG, getPIDController().setFF(value, slot));
    }

    @Override
    public void setSelectedFeedbackSensor(final FeedbackDevice feedbackDevice) {
        switch (feedbackDevice) {
            case Analog:
                {
                    SparkAnalogSensor analog = getAnalog(SparkAnalogSensor.Mode.kAbsolute);
                    revErrorToException(ExceptionTarget.LOG, analog.setInverted(sensorInverted));
                    sensorPositionSupplier = analog::getPosition;
                    sensorVelocitySupplier = analog::getVelocity;
                    sensorPositionSetter = (pos) -> REVLibError.kOk;
                    sensorInvertedSetter = analog::setInverted;
                    revErrorToException(
                            ExceptionTarget.LOG, getPIDController().setFeedbackDevice(analog));
                    return;
                }
            case CTRE_MagEncoder_Absolute:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder"));
            case CTRE_MagEncoder_Relative:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support CTRE Mag Encoder"));
            case IntegratedSensor:
                {
                    RelativeEncoder encoder = getEncoder();
                    // NOTE(rcahoon, 2022-04-19): Don't call this. Trying to call setInverted on the
                    // integrated sensor returns an error.
                    // revErrorToException(ExceptionTarget.LOG,
                    // encoder.setInverted(sensorInverted));
                    sensorPositionSupplier = encoder::getPosition;
                    sensorVelocitySupplier = encoder::getVelocity;
                    sensorPositionSetter = encoder::setPosition;
                    // NOTE(rcahoon, 2022-04-19): Don't call this. Trying to call setInverted on the
                    // integrated sensor returns an error.
                    // sensorInvertedSetter = encoder::setInverted;
                    sensorInvertedSetter = (inverted) -> REVLibError.kOk;
                    revErrorToException(
                            ExceptionTarget.LOG, getPIDController().setFeedbackDevice(encoder));
                    return;
                }
            case None:
                return;
            case PulseWidthEncodedPosition:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support PWM sensors"));
            case QuadEncoder:
                // TODO: should we pass a real counts-per-rev scale here?
                RelativeEncoder encoder = getAlternateEncoder(1);
                revErrorToException(ExceptionTarget.LOG, encoder.setInverted(sensorInverted));
                sensorPositionSupplier = encoder::getPosition;
                sensorVelocitySupplier = encoder::getVelocity;
                sensorPositionSetter = encoder::setPosition;
                sensorInvertedSetter = encoder::setInverted;
                revErrorToException(
                        ExceptionTarget.LOG, getPIDController().setFeedbackDevice(encoder));
                return;
            case RemoteSensor0:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support remote sensors"));
            case RemoteSensor1:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support remote sensors"));
            case SensorDifference:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support SensorDifference"));
            case SensorSum:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support SensorSum"));
            case SoftwareEmulatedSensor:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException(
                                "SparkMax does not support SoftwareEmulatedSensor"));
            case Tachometer:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("SparkMax does not support Tachometer"));
            default:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("Unsupported sensor type " + feedbackDevice));
        }
    }

    @Override
    public void setSensorInverted(final boolean inverted) {
        sensorInverted = inverted;
        revErrorToException(ExceptionTarget.LOG, sensorInvertedSetter.apply(inverted));
    }

    @Override
    public void setOutputRange(
            ValueProvider<Double> minOutput, ValueProvider<Double> maxOutput, int slot) {
        pidSlotHelper.setOutputRange(minOutput, maxOutput, slot);
    }

    @Override
    public void setOutputRange_Impl(final double minOutput, final double maxOutput, int slot) {
        revErrorToException(
                ExceptionTarget.LOG, getPIDController().setOutputRange(minOutput, maxOutput, slot));
    }

    public void setCurrentLimit(final double ampsLimit) {
        revErrorToException(ExceptionTarget.LOG, setSmartCurrentLimit((int) ampsLimit));
    }

    @Override
    public void restoreFactoryDefault() {
        revErrorToException(ExceptionTarget.LOG, restoreFactoryDefaults());
    }

    @Override
    public void setOpenLoopRamp(final double secondsFromNeutralToFull) {
        revErrorToException(ExceptionTarget.LOG, setOpenLoopRampRate(secondsFromNeutralToFull));
    }

    @Override
    public void setClosedLoopRamp(final double secondsFromNeutralToFull) {
        revErrorToException(ExceptionTarget.LOG, setClosedLoopRampRate(secondsFromNeutralToFull));
    }
}
