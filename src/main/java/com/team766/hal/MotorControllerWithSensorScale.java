package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

// TODO: add support for live refreshes of PID values, via PIDSlotHelper?
public class MotorControllerWithSensorScale implements MotorController {
    private MotorController delegate;
    private double scale;

    public MotorControllerWithSensorScale(final MotorController delegate_, final double scale_) {
        this.delegate = delegate_;
        this.scale = scale_;
    }

    @Override
    public int numPIDSlots() {
        return delegate.numPIDSlots();
    }

    @Override
    public double getSensorPosition() {
        return delegate.getSensorPosition() * scale;
    }

    @Override
    public double getSensorVelocity() {
        return delegate.getSensorVelocity() * scale;
    }

    @Override
    public void set(
            final ControlMode mode, final double value, int slot, double arbitraryFeedForward) {
        switch (mode) {
            case PercentOutput:
                delegate.set(mode, value);
                return;
            case Position:
                delegate.set(mode, value / scale, slot, arbitraryFeedForward);
                return;
            case Velocity:
                delegate.set(mode, value / scale, slot, arbitraryFeedForward);
                return;
            case Voltage:
                delegate.set(mode, value);
                return;
            case Disabled:
                delegate.set(mode, value);
                return;
            default:
                break;
        }
        throw new UnsupportedOperationException(
                "Unimplemented control mode in MotorControllerWithSensorScale");
    }

    @Override
    public void setInverted(final boolean isInverted) {
        delegate.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return delegate.getInverted();
    }

    @Override
    public void stopMotor() {
        delegate.stopMotor();
    }

    @Override
    public void setSensorPosition(final double position) {
        delegate.setSensorPosition(position / scale);
    }

    @Override
    public void follow(final MotorController leader) {
        delegate.follow(leader);
    }

    @Override
    public void setNeutralMode(final NeutralMode neutralMode) {
        delegate.setNeutralMode(neutralMode);
    }

    @Override
    public void setP(final double value, int slot) {
        delegate.setP(value * scale, slot);
    }

    @Override
    public void setI(final double value, int slot) {
        delegate.setI(value * scale, slot);
    }

    @Override
    public void setD(final double value, int slot) {
        delegate.setD(value * scale, slot);
    }

    @Override
    public void setFF(final double value, int slot) {
        delegate.setFF(value * scale, slot);
    }

    @Override
    public void setSelectedFeedbackSensor(final FeedbackDevice feedbackDevice) {
        delegate.setSelectedFeedbackSensor(feedbackDevice);
    }

    @Override
    public void setSensorInverted(final boolean inverted) {
        delegate.setSensorInverted(inverted);
    }

    @Override
    public void setOutputRange(final double minOutput, final double maxOutput, int slot) {
        delegate.setOutputRange(minOutput, maxOutput, slot);
    }

    @Override
    public void setCurrentLimit(final double ampsLimit) {
        delegate.setCurrentLimit(ampsLimit);
    }

    @Override
    public void restoreFactoryDefault() {
        delegate.restoreFactoryDefault();
    }

    @Override
    public void setOpenLoopRamp(final double secondsFromNeutralToFull) {
        delegate.setOpenLoopRamp(secondsFromNeutralToFull);
    }

    @Override
    public void setClosedLoopRamp(final double secondsFromNeutralToFull) {
        delegate.setClosedLoopRamp(secondsFromNeutralToFull);
    }

    @Override
    public double get() {
        return delegate.get();
    }

    @Override
    public void set(final double power) {
        delegate.set(power);
    }
}
