package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.controllers.PIDController;
import com.team766.framework.Scheduler;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class LocalMotorController implements MotorController {
    private BasicMotorController motor;
    private ControlInputReader sensor;
    private PIDController pidController;

    private boolean inverted = false;
    private boolean sensorInverted = false;
    private double sensorOffset = 0.0;

    private ControlMode controlMode = ControlMode.PercentOutput;
    private double setpoint = 0.0;
    private MotorController leader = null;

    public LocalMotorController(
            String configPrefix,
            final BasicMotorController motor_,
            final ControlInputReader sensor_) {
        this.motor = motor_;
        this.sensor = sensor_;

        if (!configPrefix.endsWith(".")) {
            configPrefix += ".";
        }
        this.pidController = PIDController.loadFromConfig(configPrefix + "pid.");

        Scheduler.getInstance()
                .add(
                        new Runnable() {
                            @Override
                            public void run() {

                                if (leader != null) {
                                    setPower(leader.get());
                                    return;
                                }

                                switch (LocalMotorController.this.controlMode) {
                                    case Disabled:
                                        // support proper output disabling if this.motor is a
                                        // MotorController
                                        if (LocalMotorController.this.motor
                                                instanceof MotorController) {
                                            ((MotorController) LocalMotorController.this.motor)
                                                    .set(ControlMode.Disabled, 0);
                                        } else {
                                            setPower(0);
                                        }
                                        break;
                                    case PercentOutput:
                                        setPower(setpoint);
                                        break;
                                    case Position:
                                        pidController.calculate(getSensorPosition());
                                        setPower(pidController.getOutput());
                                        break;
                                    case Velocity:
                                        pidController.calculate(getSensorVelocity());
                                        setPower(pidController.getOutput());
                                        break;
                                    case Voltage:
                                        setPower(
                                                setpoint
                                                        / RobotProvider.instance
                                                                .getBatteryVoltage());
                                        break;
                                    default:
                                        LoggerExceptionUtils.logException(
                                                new UnsupportedOperationException(
                                                        toString()
                                                                + " does not support control mode "
                                                                + LocalMotorController.this
                                                                        .controlMode));
                                        break;
                                }
                            }

                            @Override
                            public String toString() {
                                return LocalMotorController.this.toString();
                            }
                        });
    }

    @Override
    public String toString() {
        return "LocalMotorController:" + LocalMotorController.this.motor.toString();
    }

    private void setPower(double power) {
        if (this.inverted) {
            power *= -1;
        }
        this.motor.set(power);
    }

    @Override
    public double get() {
        double value = motor.get();
        if (this.inverted) {
            value *= -1;
        }
        return value;
    }

    @Override
    public void set(final double power) {
        set(ControlMode.PercentOutput, power);
    }

    @Override
    public void setInverted(final boolean isInverted) {
        this.inverted = isInverted;
    }

    @Override
    public boolean getInverted() {
        return this.inverted;
    }

    @Override
    public void stopMotor() {
        set(ControlMode.PercentOutput, 0);
    }

    @Override
    public void setSensorPosition(double position) {
        if (this.sensor == null) {
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            toString() + " does not have an attached sensor configured");
            return;
        }
        if (this.sensorInverted != this.inverted) {
            position *= -1;
        }
        sensorOffset = position - sensor.getPosition();
    }

    @Override
    public double getSensorPosition() {
        if (this.sensor == null) {
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            toString() + " does not have an attached sensor configured");
            return 0.0;
        }
        double position = sensor.getPosition() + sensorOffset;
        if (this.sensorInverted != this.inverted) {
            position *= -1;
        }
        return position;
    }

    @Override
    public double getSensorVelocity() {
        if (this.sensor == null) {
            Logger.get(Category.CONFIGURATION)
                    .logRaw(
                            Severity.ERROR,
                            toString() + " does not have an attached sensor configured");
            return 0.0;
        }
        double velocity = sensor.getRate();
        if (this.sensorInverted != this.inverted) {
            velocity *= -1;
        }
        return velocity;
    }

    @Override
    public void set(final ControlMode mode, final double value) {
        if (this.controlMode != mode || this.leader != null) {
            pidController.reset();
            this.leader = null;
        }
        this.controlMode = mode;
        this.setpoint = value;
        this.pidController.setSetpoint(setpoint);
    }

    public ControlMode getControlMode() {
        return this.controlMode;
    }

    @Override
    public void follow(final MotorController leader_) {
        if (leader_ == null) {
            throw new IllegalArgumentException("leader argument to follow() is null");
        }
        // TODO: detect if this.motor is a MotorController, and delegate to its follow() method if
        // so.
        this.leader = leader_;
    }

    @Override
    public void setNeutralMode(final NeutralMode neutralMode) {
        if (this.motor instanceof MotorController) {
            ((MotorController) this.motor).setNeutralMode(neutralMode);
        } else {
            LoggerExceptionUtils.logException(
                    new UnsupportedOperationException(
                            this.toString()
                                    + " - setNeutralMode() is only supported with CAN motor controllers"));
        }
    }

    @Override
    public void setP(final double value) {
        pidController.setP(value);
    }

    @Override
    public void setI(final double value) {
        pidController.setI(value);
    }

    @Override
    public void setD(final double value) {
        pidController.setD(value);
    }

    @Override
    public void setFF(final double value) {
        pidController.setFF(value);
    }

    @Override
    public void setSelectedFeedbackSensor(final FeedbackDevice feedbackDevice) {
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setSelectedFeedbsckSensor() is currently unsupported by LocalMotorController"));
    }

    @Override
    public void setSensorInverted(final boolean inverted_) {
        this.sensorInverted = inverted_;
    }

    @Override
    public void setOutputRange(final double minOutput, final double maxOutput) {
        pidController.setMaxoutputLow(minOutput);
        pidController.setMaxoutputHigh(maxOutput);
    }

    @Override
    public void setCurrentLimit(final double ampsLimit) {
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setCurrentLimit() is currently unsupported by LocalMotorController"));
    }

    @Override
    public void restoreFactoryDefault() {
        this.motor.restoreFactoryDefault();

        this.setP(0.0);
        this.setI(0.0);
        this.setD(0.0);
        this.setFF(0.0);
        this.pidController.setMaxoutputLow(null);
        this.pidController.setMaxoutputHigh(null);

        this.inverted = false;
        this.sensorInverted = false;
        this.controlMode = ControlMode.Disabled;
        this.setpoint = 0.0;
    }

    @Override
    public void setOpenLoopRamp(final double secondsFromNeutralToFull) {
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setOpenLoopRamp() is currently unsupported by LocalMotorController"));
    }

    @Override
    public void setClosedLoopRamp(final double secondsFromNeutralToFull) {
        LoggerExceptionUtils.logException(
                new UnsupportedOperationException(
                        "setClosedLoopRamp() is currently unsupported by LocalMotorController"));
    }
}
