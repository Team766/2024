package com.team766.controllers;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.library.ValueProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.function.Supplier;

/**
 * Helper class that can set a setpoint in a PID loop, eg within {@link Mechanism.run}.
 *
 * Uses functional interfaces for getting the setpoint, output, PID slot, and feedforward.
 * Can use with lambdas and/or method references, eg:
 *
 * PIDRunner pidRunner = new PIDRunner("SHOULDER", leftMotor, ControlMode.Position,
 *                                     this::getSetPoint, this::getAngle);
 */
public class PIDRunner {

    public static Supplier<Integer> DEFAULT_SLOT_PICKER = () -> 1;
    public static Supplier<Double> NO_FEED_FORWARD = () -> 0.0;

    /**
     * Returns a fixed FeedForward supplier, simply returning the latest input ffGain from a config file.
     * @param ffGain Input FeedForward Gain, read from a config file.
     * @return Fixed FeedForward Gain from the current value of ffGain.
     */
    public static Supplier<Double> fixedFeedForward(ValueProvider<Double> ffGain) {
        return () -> ffGain.get();
    }

    /**
     * Returns a FeedForward supplier that gets the latest input ffGain from a config file and returns a proportional
     * FeedForward based on the cosine of the supplied angle.  Useful for arm type mechanisms, where we want to
     * counteract gravity proportionally to the arm's current angle, where 0 is parallel to the ground and 90
     * is perpendicular & up.
     *
     * @param ffGain Input FeedForward Gain, read from a config file.
     * @param angle Current angle of the mechanism.  0 is parallel to the ground, 90 is perpendicular & up.
     * @return Proportional FeedForward Gain based on the angle.
     */
    public static Supplier<Double> cosineFeedForward(
            ValueProvider<Double> ffGain, Supplier<Double> angle) {
        return () -> ffGain.valueOr(0.0) * Math.cos(Math.toRadians(angle.get()));
    }

    private final String label;
    private final MotorController motor;
    private final MotorController.ControlMode mode;
    private final Supplier<Double> setPoint;
    private final Supplier<Double> output;
    private final Supplier<Integer> slot;
    private final Supplier<Double> feedForward;
    private double prevSetPoint = 0.0;
    private double prevOutput = 0.0;

    public PIDRunner(
            String label,
            MotorController motor,
            MotorController.ControlMode mode,
            Supplier<Double> setPoint,
            Supplier<Double> output) {
        this(label, motor, mode, setPoint, output, DEFAULT_SLOT_PICKER, NO_FEED_FORWARD);
    }

    public PIDRunner(
            String label,
            MotorController motor,
            MotorController.ControlMode mode,
            Supplier<Double> setPoint,
            Supplier<Double> output,
            Supplier<Integer> slot,
            Supplier<Double> feedForward) {
        this.label = label;
        this.motor = motor;
        this.mode = mode;
        this.setPoint = setPoint;
        this.output = output;
        this.slot = slot;
        this.feedForward = feedForward;
    }

    public void run() {
        double currentSetPoint = setPoint.get();
        double currentOutput = output.get();
        if (prevSetPoint != currentOutput || prevOutput != currentOutput) {
            prevSetPoint = currentSetPoint;
            prevOutput = currentOutput;
            SmartDashboard.putNumber(label + " setpoint", currentSetPoint);
            SmartDashboard.putNumber(label + " output", currentOutput);
            // FIXME: switch to supporting slot, feedForward once integrated
            // motor.set(mode, currentSetPoint, slot.get(), feedForward.get());
            motor.set(mode, currentSetPoint);
        }
    }
}
