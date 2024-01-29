package com.team766.hal.wpilib;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.team766.hal.MotorController;
import com.team766.hal.MotorControllerCommandFailedException;
import com.team766.logging.LoggerExceptionUtils;

public class CANTalonFxMotorController extends TalonFX implements MotorController {

    private TalonFXConfiguration talonFXConfig = new TalonFXConfiguration();

    // TODO: add support for taking a CANcoder as a ctor parameter
    public CANTalonFxMotorController(final int deviceNumber, final String canBus) {
        super(deviceNumber, canBus);
        TalonFXConfigurator configurator = getConfigurator();
        // TODO: log instead?
        statusCodeToException(ExceptionTarget.THROW, configurator.refresh(talonFXConfig));
    }

    public CANTalonFxMotorController(final int deviceNumber) {
        this(deviceNumber, "");
    }

    private enum ExceptionTarget {
        THROW,
        LOG,
    }

    private static void statusCodeToException(
            final ExceptionTarget throwEx, final StatusCode code) {
        if (code.isOK()) {
            return;
        }
        var ex = new MotorControllerCommandFailedException(code.toString());
        switch (throwEx) {
            case THROW:
                throw ex;
            default:
            case LOG:
                LoggerExceptionUtils.logException(ex);
                break;
        }
    }

    private void refreshConfig() {
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().refresh(talonFXConfig));
    }

    @Override
    public void set(final ControlMode mode, double value) {
        switch (mode) {
            case Disabled:
                super.disable();
                return;
            case PercentOutput:
                DutyCycleOut percent = new DutyCycleOut(value);
                super.setControl(percent);
                break;
            case Position:
                PositionDutyCycle position = new PositionDutyCycle(value);
                super.setControl(position);
                break;
            case Velocity:
                VelocityDutyCycle velocity = new VelocityDutyCycle(value);
                super.setControl(velocity);
                break;
            case Voltage:
                VoltageOut voltage = new VoltageOut(value);
                super.setControl(voltage);
            default:
                throw new IllegalArgumentException("Unsupported control mode " + mode);
        }
    }

    @Override
    public double getSensorPosition() {
        // TODO: get the StatusSignal once and refresh, instead?
        StatusSignal<Double> status = super.getPosition();
        StatusCode code = status.getStatus();
        if (code.isOK()) {
            return status.getValueAsDouble();
        } else {
            statusCodeToException(ExceptionTarget.LOG, code);
            return 0.0;
        }
    }

    @Override
    public double getSensorVelocity() {
        // TODO: get the StatusSignal once and refresh, instead?
        StatusSignal<Double> status = super.getVelocity();
        StatusCode code = status.getStatus();
        if (code.isOK()) {
            return status.getValueAsDouble();
        } else {
            statusCodeToException(ExceptionTarget.LOG, code);
            return 0.0;
        }
    }

    @Override
    public void setSensorPosition(final double position) {
        statusCodeToException(ExceptionTarget.THROW, super.setPosition(position));
    }

    @Override
    public void follow(final MotorController leader) {
        if (leader instanceof CANTalonFxMotorController) {
            CANTalonFxMotorController talonFxLeader = (CANTalonFxMotorController) leader;
            StrictFollower follower = new StrictFollower(talonFxLeader.getDeviceID());
            statusCodeToException(ExceptionTarget.LOG, super.setControl(follower));
        } else {
            LoggerExceptionUtils.logException(
                    new IllegalArgumentException(
                            "TalonFX can only follow another TalonFX motor controller."));
        }
    }

    @Override
    public void setOpenLoopRamp(final double secondsFromNeutralToFull) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        talonFXConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = secondsFromNeutralToFull;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    @Override
    public void setClosedLoopRamp(final double secondsFromNeutralToFull) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        talonFXConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = secondsFromNeutralToFull;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    @Override
    public void setFF(final double value) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        // TODO: should this be kV or kS?
        // https://pro.docs.ctr-electronics.com/en/latest/docs/migration/migration-guide/closed-loop-guide.html says kV
        talonFXConfig.Slot0.kS = value;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    @Override
    public void setP(final double value) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        talonFXConfig.Slot0.kP = value;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    @Override
    public void setI(final double value) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        talonFXConfig.Slot0.kI = value;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    @Override
    public void setD(final double value) {
        refreshConfig(); // necessary?  I don't *think* this should be modified external to this
        // code.
        talonFXConfig.Slot0.kD = value;
        statusCodeToException(ExceptionTarget.LOG, getConfigurator().apply(talonFXConfig.Slot0));
    }

    private void setRemoteFeedbackSensor(CANcoder canCoder) {
        FeedbackConfigs feedback = new FeedbackConfigs();
        feedback.FeedbackRemoteSensorID = canCoder.getDeviceID();
        feedback.FeedbackSensorSource = FeedbackSensorSourceValue.RemoteCANcoder;
        statusCodeToException(ExceptionTarget.THROW, getConfigurator().apply(feedback));
    }

    @Override
    public void setSelectedFeedbackSensor(final FeedbackDevice feedbackDevice) {
        // TODO: add support for this.
        // doing so will require extending MotorController.setSelectedFeedbackSensor() to take an
        // optional deviceID.
        // alternatively, we may only allow specifying the CANcoder in the constructor,
        // eg if configured as a "child" node of this motor in the MaroonFramework config.
        // NOTE: the only remote sensor that's supported is a CANcoder.
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setSelectedFeedbackSensor() is not currently supported."));
    }

    @Override
    public void setSensorInverted(final boolean inverted) {
        // does not appear to be supported on the TalonFX.  developers need to invert the remote
        // sensor (CANcoder)
        // directly via the CANcoder.
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setSelectedFeedbackSensor() is not currently supported."));
    }

    @Override
    public void setOutputRange(final double minOutput, final double maxOutput) {
        VoltageConfigs voltage =
                new VoltageConfigs()
                        .withPeakReverseVoltage(minOutput)
                        .withPeakForwardVoltage(maxOutput);
        statusCodeToException(ExceptionTarget.LOG, super.getConfigurator().apply(voltage));
    }

    @Override
    public void setCurrentLimit(final double ampsLimit) {
        CurrentLimitsConfigs config =
                new CurrentLimitsConfigs()
                        .withSupplyCurrentLimit(ampsLimit)
                        .withSupplyCurrentLimitEnable(true);
        statusCodeToException(ExceptionTarget.LOG, super.getConfigurator().apply(config));
    }

    @Override
    public void restoreFactoryDefault() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        StatusCode code = super.getConfigurator().apply(talonFXConfig);
        if (code.isOK()) {
            talonFXConfig = config;
        } else {
            statusCodeToException(ExceptionTarget.LOG, code);
        }
    }

    @Override
    public void setNeutralMode(final NeutralMode neutralMode) {
        NeutralModeValue neutralModeValue;

        switch (neutralMode) {
            case Coast:
                neutralModeValue = NeutralModeValue.Coast;
                break;
            case Brake:
                neutralModeValue = NeutralModeValue.Brake;
                break;
            default:
                LoggerExceptionUtils.logException(
                        new IllegalArgumentException("Unsupported neutral mode " + neutralMode));
                return;
        }
        super.setNeutralMode(neutralModeValue);
    }
}
