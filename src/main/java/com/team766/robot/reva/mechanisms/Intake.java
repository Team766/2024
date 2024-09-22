package com.team766.robot.reva.mechanisms;

import static com.team766.framework.StatusBus.getStatusOrThrow;
import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.framework.Request;
import com.team766.framework.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.ValueProvider;

public class Intake extends Mechanism<Intake.IntakeRequest, Intake.IntakeStatus> {
    public record IntakeStatus(double motorPower, boolean hasNoteInIntake, boolean isNoteClose)
            implements Status {}

    public sealed interface IntakeRequest extends Request {}

    public record In() implements IntakeRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record Out() implements IntakeRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record Stop() implements IntakeRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public static IntakeRequest makeNudgeUp() {
        return new SetMotorPower(Math.min(
                getStatusOrThrow(IntakeStatus.class).motorPower() + NUDGE_INCREMENT, MAX_POWER));
    }

    public static IntakeRequest makeNudgeDown() {
        return new SetMotorPower(Math.max(
                getStatusOrThrow(IntakeStatus.class).motorPower() - NUDGE_INCREMENT, MIN_POWER));
    }

    public record SetMotorPower(double power) implements IntakeRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record SetPowerForSensorDistance() implements IntakeRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

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
    private static final double IS_CLOSE_THRESHOLD = 350;

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
    protected IntakeRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected IntakeRequest getIdleRequest() {
        return new Stop();
    }

    @Override
    protected IntakeStatus run(IntakeRequest request, boolean isRequestNew) {
        // SmartDashboard.putNumber("[INTAKE POWER]", intakePower);
        // SmartDashboard.putNumber("[INTAKE] Current", MotorUtil.getCurrentUsage(intakeMotor));
        // SmartDashboard.putNumber("Prox Sensor", sensor.getRange());

        switch (request) {
            case In g -> {
                if (!isRequestNew) break;
                intakeMotor.set(DEFAULT_POWER);
            }
            case Out g -> {
                if (!isRequestNew) break;
                intakeMotor.set(-1 * DEFAULT_POWER);
            }
            case Stop g -> {
                if (!isRequestNew) break;
                intakeMotor.set(0.0);
            }
            case SetMotorPower g -> {
                if (!isRequestNew) break;
                intakeMotor.set(g.power());
            }
            case SetPowerForSensorDistance g -> {
                intakeMotor.set(com.team766.math.Math.interpolate(
                        positions,
                        sensor.getRange(),
                        IntakePosition::proximityValue,
                        IntakePosition::intakePower));
            }
        }

        return new IntakeStatus(
                intakeMotor.get(),
                (threshold.get()) > sensor.getRange() && sensor.isRangeValid(),
                (IS_CLOSE_THRESHOLD) > sensor.getRange() && sensor.isRangeValid());
    }
}
