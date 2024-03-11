package com.team766.hal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.library.SetValueProvider;
import com.team766.library.ValueProvider;

/**
 * Interface for motor controlling devices.
 */
public interface MotorController extends BasicMotorController {

    enum Type {
        VictorSP,
        VictorSPX,
        TalonSRX,
        SparkMax,
        TalonFX,
    }

    enum ControlMode {
        PercentOutput,
        Position,
        Velocity,
        Voltage,
        Disabled,
    }

    /**
     * Common interface for setting the power output by a motor controller.
     *
     * @param power The power to set. Value should be between -1.0 and 1.0.
     */
    void set(double power);

    /**
     * Sets the appropriate output on the motor controller, depending on the mode.
     * @param mode The output mode to apply.
     * In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
     * In Current mode, output value is in amperes.
     * In Velocity mode, output value is in position change / 100ms.
     * In Position mode, output value is in encoder ticks or an analog value,
     * depending on the sensor.
     * In Follower mode, the output value is the integer device ID of the talon to
     * duplicate.
     *
     * @param value The setpoint value, as described above.
     */
    default void set(ControlMode mode, double value) {
        set(mode, value, 0, 0.0);
    }

    /**
     * Sets the appropriate output on the motor controller, depending on the mode.
     * @param mode The output mode to apply.
     * In PercentOutput, the output is between -1.0 and 1.0, with 0.0 as stopped.
     * In Current mode, output value is in amperes.
     * In Velocity mode, output value is in position change / 100ms.
     * In Position mode, output value is in encoder ticks or an analog value,
     * depending on the sensor.
     * In Follower mode, the output value is the integer device ID of the talon to
     * duplicate.
     *
     * @param value The setpoint value, as described above.
     * @param slot The PID slot to use, if the mode uses PID.
     * @param arbitraryFeedForward An arbitrary feed forward value to use when calculating PID.
     */
    void set(ControlMode mode, double value, int slot, double arbitraryFeedForward);

    /**
     * Common interface for inverting direction of a motor controller.
     *
     * This changes the direction of the motor and sensor together. To change the
     * direction of the sensor relative to the direction of the motor,
     * use setSensorInverted.
     *
     * @param isInverted The state of inversion true is inverted.
     */
    void setInverted(boolean isInverted);

    /**
     * Common interface for returning if a motor controller is in the inverted
     * state or not.
     *
     * @return isInverted The state of the inversion true is inverted.
     */
    boolean getInverted();

    /**
     * Stops motor movement. Motor can be moved again by calling set without having
     * to re-enable the motor.
     */
    void stopMotor();

    /**
     * Read the motor position from the sensor attached to the motor controller.
     */
    double getSensorPosition();

    /**
     * Read the motor velocity from the sensor attached to the motor controller.
     */
    double getSensorVelocity();

    /**
     * Sets the motors encoder value to the given position.
     *
     * @param position The desired set position
     */
    void setSensorPosition(double position);

    void follow(MotorController leader);

    void setNeutralMode(NeutralMode neutralMode);

    default int numPIDSlots() {
        return 1;
    }

    void setP(ValueProvider<Double> value, int slot);

    default void setP(double value, int slot) {
        setP(new SetValueProvider<Double>(value), slot);
    }

    void setI(ValueProvider<Double> value, int slot);

    default void setI(double value, int slot) {
        setI(new SetValueProvider<Double>(value), slot);
    }

    void setD(ValueProvider<Double> value, int slot);

    default void setD(double value, int slot) {
        setD(new SetValueProvider<Double>(value), slot);
    }

    void setFF(ValueProvider<Double> value, int slot);

    default void setFF(double value, int slot) {
        setFF(new SetValueProvider<Double>(value), slot);
    }

    void setSelectedFeedbackSensor(FeedbackDevice feedbackDevice);

    /**
     * Set whether to reverse the sensor relative to the direction of the motor.
     *
     * This is different from setInverted, which sets the direction of both the
     * motor and sensor together.
     *
     * @param inverted The state of inversion true is inverted.
     */
    void setSensorInverted(boolean inverted);

    void setOutputRange(ValueProvider<Double> minOutput, ValueProvider<Double> maxOutput, int slot);

    default void setOutputRange(double minOutput, double maxOutput, int slot) {
        setOutputRange(
                new SetValueProvider<Double>(minOutput),
                new SetValueProvider<Double>(maxOutput),
                slot);
    }

    void setCurrentLimit(double ampsLimit);

    void restoreFactoryDefault();

    void setOpenLoopRamp(double secondsFromNeutralToFull);

    void setClosedLoopRamp(double secondsFromNeutralToFull);
}
