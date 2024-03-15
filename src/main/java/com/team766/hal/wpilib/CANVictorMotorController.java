package com.team766.hal.wpilib;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.team766.hal.MotorController;
import com.team766.hal.PIDSlotHelper;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class CANVictorMotorController extends BaseCTREMotorController
        implements MotorController, PIDSlotHelper.MotorCallbacks {
    private static final int NUM_PID_SLOTS = 2;

    private final WPI_VictorSPX m_device;
    private final PIDSlotHelper pidSlotHelper;

    public CANVictorMotorController(final int deviceNumber) {
        m_device = new WPI_VictorSPX(deviceNumber);
        pidSlotHelper = new PIDSlotHelper(this);
    }

    @Override
    public void set(final ControlMode mode, double value, int slot, double arbitraryrFeedForward) {
        m_device.selectProfileSlot(slot, 0 /* primary closed loop */);

        com.ctre.phoenix.motorcontrol.ControlMode ctre_mode = null;
        boolean useFourTermSet = true;
        switch (mode) {
            case PercentOutput:
                ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
                useFourTermSet = false;
                break;
            case Position:
                ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Position;
                break;
            case Velocity:
                // Sensor velocity is measured in units per 100ms.
                value /= 10.0;
                ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
                break;
            case Voltage:
                m_device.setVoltage(value);
                return;
            case Disabled:
                ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
                useFourTermSet = false;
                break;
            default:
                LoggerExceptionUtils.logException(
                        new UnsupportedOperationException(
                                "invalid mode provided. provided value: " + mode));
                break;
        }
        if (ctre_mode == null) {
            Logger.get(Category.HAL)
                    .logRaw(Severity.ERROR, "CAN ControlMode is not translatable: " + mode);
            ctre_mode = com.ctre.phoenix.motorcontrol.ControlMode.Disabled;
        }
        if (useFourTermSet) {
            m_device.set(ctre_mode, value, DemandType.ArbitraryFeedForward, arbitraryrFeedForward);
        } else {
            m_device.set(ctre_mode, value);
        }
    }

    @Override
    public void stopMotor() {
        m_device.set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, 0);
    }

    @Override
    public double getSensorPosition() {
        return m_device.getSelectedSensorPosition(0);
    }

    @Override
    public double getSensorVelocity() {
        // Sensor velocity is returned in units per 100ms.
        return m_device.getSelectedSensorVelocity(0) * 10.0;
    }

    @Override
    public void setSensorPosition(final double position) {
        errorCodeToException(
                ExceptionTarget.THROW, m_device.setSelectedSensorPosition(position, 0, 20));
    }

    @Override
    public void follow(final MotorController leader) {
        try {
            m_device.follow((IMotorController) leader);
        } catch (ClassCastException ex) {
            LoggerExceptionUtils.logException(
                    new IllegalArgumentException(
                            "Victor can only follow another CTRE motor controller", ex));
        }
    }

    @Override
    public void setOpenLoopRamp(final double secondsFromNeutralToFull) {
        errorCodeToException(
                ExceptionTarget.LOG,
                m_device.configOpenloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
    }

    @Override
    public void setClosedLoopRamp(final double secondsFromNeutralToFull) {
        errorCodeToException(
                ExceptionTarget.LOG,
                m_device.configClosedloopRamp(secondsFromNeutralToFull, TIMEOUT_MS));
    }

    @Override
    public void setFF(final ValueProvider<Double> value, int slot) {
        pidSlotHelper.setFF(value, slot);
    }

    @Override
    public void setFF_Impl(final double value, int slot) {
        errorCodeToException(ExceptionTarget.LOG, m_device.config_kF(slot, value, TIMEOUT_MS));
    }

    @Override
    public double get() {
        return m_device.get();
    }

    @Override
    public void set(final double power) {
        m_device.set(power);
    }

    @Override
    public void setInverted(final boolean isInverted) {
        m_device.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return m_device.getInverted();
    }

    @Override
    public void setNeutralMode(final NeutralMode neutralMode) {
        m_device.setNeutralMode(neutralMode);
    }

    @Override
    public int numPIDSlots() {
        return NUM_PID_SLOTS;
    }

    @Override
    public void setP(final ValueProvider<Double> value, int slot) {
        pidSlotHelper.setP(value, slot);
    }

    @Override
    public void setP_Impl(final double value, int slot) {
        errorCodeToException(ExceptionTarget.LOG, m_device.config_kP(slot, value, TIMEOUT_MS));
    }

    @Override
    public void setI(final ValueProvider<Double> value, int slot) {
        pidSlotHelper.setI(value, slot);
    }

    @Override
    public void setI_Impl(final double value, int slot) {
        errorCodeToException(ExceptionTarget.LOG, m_device.config_kI(slot, value, TIMEOUT_MS));
    }

    @Override
    public void setD(final ValueProvider<Double> value, int slot) {
        pidSlotHelper.setD(value, slot);
    }

    @Override
    public void setD_Impl(final double value, int slot) {
        errorCodeToException(ExceptionTarget.LOG, m_device.config_kD(slot, value, TIMEOUT_MS));
    }

    @Override
    public void setSelectedFeedbackSensor(final FeedbackDevice feedbackDevice) {
        errorCodeToException(
                ExceptionTarget.LOG, m_device.configSelectedFeedbackSensor(feedbackDevice));
    }

    @Override
    public void setSensorInverted(final boolean inverted) {
        m_device.setSensorPhase(inverted);
    }

    @Override
    public void setOutputRange(
            ValueProvider<Double> minOutput, ValueProvider<Double> maxOutput, int slot) {
        pidSlotHelper.setOutputRange(minOutput, maxOutput, slot);
    }

    @Override
    public void setOutputRange_Impl(final double minOutput, final double maxOutput, int slot) {
        if (slot != 0) {
            Logger.get(Category.HAL)
                    .logRaw(
                            Severity.WARNING,
                            "Ignoring slot for setOutputRange - unsupported on Talon");
        }
        errorCodeToException(ExceptionTarget.LOG, m_device.configPeakOutputReverse(minOutput));
        errorCodeToException(ExceptionTarget.LOG, m_device.configPeakOutputForward(maxOutput));
    }

    @Override
    public void setCurrentLimit(final double ampsLimit) {
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException("VictorSPX does not support current limiting"));
    }

    @Override
    public void restoreFactoryDefault() {
        errorCodeToException(ExceptionTarget.LOG, m_device.configFactoryDefault());
    }
}
