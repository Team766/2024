package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Subsystem;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.ValueProvider;
import org.littletonrobotics.junction.AutoLogOutput;

public class Intake extends Subsystem<Intake.Status, Intake.Goal> {
    public record Status(
            @AutoLogOutput boolean hasNoteInIntake, @AutoLogOutput boolean isNoteClose) {}

    public sealed interface Goal {}

    public record In() implements Goal {}

    public record Out() implements Goal {}

    public record Stop() implements Goal {}

    public record NudgeUp() implements Goal {}

    public record NudgeDown() implements Goal {}

    public record SetPowerForSensorDistance() implements Goal {}

    private record IntakePosition(double intakePower, double proximityValue) {}

    IntakePosition[] positions = new IntakePosition[] {
        new IntakePosition(0, 150),
        new IntakePosition(0.2, 200),
        new IntakePosition(0.4, 400),
        new IntakePosition(1.0, 480)
    };

    private static final double DEFAULT_POWER = 1.0;
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double CURRENT_LIMIT = 30.0; // a little lower than max efficiency
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1 * MAX_POWER;
    private static final double IS_CLOSE_THRESHOLD = 200;

    // This should be the amount that getRange() should return less than for a note to be classified
    // as in
    private static ValueProvider<Double> threshold = ConfigFileReader.getInstance()
            .getDouble("RightProximitySensor.threshold"); // needs calibration

    private MotorController intakeMotor;
    private TimeOfFlight sensor;

    public Intake() {
        intakeMotor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
        intakeMotor.setNeutralMode(NeutralMode.Brake);
        intakeMotor.setCurrentLimit(CURRENT_LIMIT);
        sensor = new TimeOfFlight(0); // needs calibration

        sensor.setRangingMode(RangingMode.Short, 24);
    }

    @Override
    protected Status updateState() {
        // SmartDashboard.putNumber("[INTAKE POWER]", intakePower);
        // SmartDashboard.putNumber("[INTAKE] Current", MotorUtil.getCurrentUsage(intakeMotor));
        // SmartDashboard.putNumber("Prox Sensor", sensor.getRange());
        return new Status(
                (threshold.get()) > sensor.getRange() && sensor.isRangeValid(),
                (IS_CLOSE_THRESHOLD) > sensor.getRange() && sensor.isRangeValid());
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        switch (goal) {
            case In g -> {
                if (!goalChanged) return;
                intakeMotor.set(DEFAULT_POWER);
            }
            case Out g -> {
                if (!goalChanged) return;
                intakeMotor.set(-1 * DEFAULT_POWER);
            }
            case Stop g -> {
                if (!goalChanged) return;
                intakeMotor.set(0.0);
            }
            case NudgeUp g -> {
                if (!goalChanged) return;
                intakeMotor.set(Math.min(intakeMotor.get() + NUDGE_INCREMENT, MAX_POWER));
            }
            case NudgeDown g -> {
                if (!goalChanged) return;
                intakeMotor.set(Math.max(intakeMotor.get() - NUDGE_INCREMENT, MIN_POWER));
            }
            case SetPowerForSensorDistance g -> {
                intakeMotor.set(com.team766.math.Math.interpolate(
                        positions,
                        sensor.getRange(),
                        IntakePosition::proximityValue,
                        IntakePosition::intakePower));
            }
        }
    }
}
