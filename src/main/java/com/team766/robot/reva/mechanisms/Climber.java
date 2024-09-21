package com.team766.robot.reva.mechanisms;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Climber extends Mechanism<Climber.ClimberRequest, Climber.ClimberStatus> {

    public record ClimberStatus(double heightLeft, double heightRight) implements Status {
        public boolean isLeftNear(MoveToPosition position) {
            return Math.abs(heightLeft() - position.height()) < POSITION_LOCATION_THRESHOLD;
        }

        public boolean isRightNear(MoveToPosition position) {
            return Math.abs(heightRight() - position.height()) < POSITION_LOCATION_THRESHOLD;
        }
    }

    public sealed interface ClimberRequest extends Request {}

    public record Stop() implements ClimberRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record MotorPowers(double powerLeft, double powerRight, boolean overrideSoftLimits)
            implements ClimberRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record MoveToPosition(double height) implements ClimberRequest {
        // A very rough measurement, and was being very safe.
        // TODO: Needs to be measured more accurately.
        public static final MoveToPosition TOP = new MoveToPosition(43.18);
        public static final MoveToPosition BOTTOM = new MoveToPosition(0);
        public static final MoveToPosition BELOW_ARM =
                new MoveToPosition(15); // TODO: Find actual value

        public double getRotations() {
            return heightToRotations(height);
        }

        @Override
        public boolean isDone() {
            return checkForStatusWith(
                    ClimberStatus.class, s -> s.isLeftNear(this) && s.isRightNear(this));
        }
    }

    private MotorController leftMotor;
    private MotorController rightMotor;

    private static final double GEAR_RATIO_AND_CIRCUMFERENCE =
            (14. / 50.) * (30. / 42.) * (1.25 * Math.PI);
    private static final double SUPPLY_CURRENT_LIMIT = 30; // max efficiency from spec sheet
    private static final double STATOR_CURRENT_LIMIT = 80; // TUNE THIS!
    private static final double POSITION_LOCATION_THRESHOLD = 1;
    private static final double INITITAL_POSITION = -63.0; // TODO: set
    private static final double NUDGE_INCREMENT = 0.1;

    private boolean softLimitsEnabled;

    public Climber() {
        leftMotor = RobotProvider.instance.getMotor(CLIMBER_LEFT_MOTOR);
        rightMotor = RobotProvider.instance.getMotor(CLIMBER_RIGHT_MOTOR);

        leftMotor.setNeutralMode(NeutralMode.Brake);
        rightMotor.setNeutralMode(NeutralMode.Brake);
        leftMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        rightMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        leftMotor.setSensorPosition(INITITAL_POSITION);
        rightMotor.setSensorPosition(INITITAL_POSITION);
        MotorUtil.setTalonFXStatorCurrentLimit(leftMotor, STATOR_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(rightMotor, STATOR_CURRENT_LIMIT);
        MotorUtil.setSoftLimits(leftMotor, 0.0 /* forward limit */, -115.0 /* reverse limit */);
        MotorUtil.setSoftLimits(rightMotor, 0.0 /* forward limit */, -115.0 /* reverse limit */);

        enableSoftLimits(true);
    }

    private void enableSoftLimits(boolean enabled) {
        MotorUtil.enableSoftLimits(leftMotor, enabled);
        MotorUtil.enableSoftLimits(rightMotor, enabled);
        softLimitsEnabled = enabled;
    }

    public void resetLeftPosition() {
        leftMotor.setSensorPosition(0);
    }

    public void resetRightPosition() {
        rightMotor.setSensorPosition(0);
    }

    private static double heightToRotations(double height) {
        return height * GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    private static double rotationsToHeight(double rotations) {
        return rotations / GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    @Override
    protected ClimberRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected ClimberStatus run(ClimberRequest request, boolean isRequestNew) {
        // SmartDashboard.putNumber("[CLIMBER] Left Rotations", leftMotor.getSensorPosition());
        // SmartDashboard.putNumber("[CLIMBER] Right Rotations", rightMotor.getSensorPosition());
        // SmartDashboard.putNumber("[CLIMBER] Left Height", getHeightLeft());
        // SmartDashboard.putNumber("[CLIMBER] Right Height", getHeightRight());
        // SmartDashboard.putNumber("[CLIMBER] Left Power", leftPower);
        // SmartDashboard.putNumber("[CLIMBER] Right Power", rightPower);
        // SmartDashboard.putNumber(
        //         "[CLIMBER] Left Motor Supply Current", MotorUtil.getCurrentUsage(leftMotor));
        // SmartDashboard.putNumber(
        //         "[CLIMBER] Right Motor Supply Current", MotorUtil.getCurrentUsage(rightMotor));
        // SmartDashboard.putNumber(
        //         "[CLIMBER] Left Motor Stator Current",
        // MotorUtil.getStatorCurrentUsage(leftMotor));
        // SmartDashboard.putNumber(
        //         "[CLIMBER] Right Motor Stator Current",
        //         MotorUtil.getStatorCurrentUsage(rightMotor));
        final var status =
                new ClimberStatus(
                        rotationsToHeight(leftMotor.getSensorPosition()),
                        rotationsToHeight(rightMotor.getSensorPosition()));

        switch (request) {
            case Stop g -> {
                leftMotor.stopMotor();
                rightMotor.stopMotor();
            }
            case MotorPowers g -> {
                boolean enableSoftLimits = !g.overrideSoftLimits();
                if (enableSoftLimits != softLimitsEnabled) {
                    enableSoftLimits(enableSoftLimits);
                }
                leftMotor.set(com.team766.math.Math.clamp(g.powerLeft(), -1, 1));
                rightMotor.set(com.team766.math.Math.clamp(g.powerRight(), -1, 1));
            }
            case MoveToPosition g -> {
                if (!softLimitsEnabled) {
                    enableSoftLimits(true);
                }

                // Control left motor
                if (status.isLeftNear(g)) {
                    leftMotor.stopMotor();
                } else if (status.heightLeft() > g.height()) {
                    // Move down
                    leftMotor.set(0.25);
                } else {
                    // Move up
                    leftMotor.set(-0.25);
                }

                // Control right motor
                if (status.isRightNear(g)) {
                    rightMotor.stopMotor();
                } else if (status.heightRight() > g.height()) {
                    // Move down
                    rightMotor.set(0.25);
                } else {
                    // Move up
                    rightMotor.set(-0.25);
                }
            }
        }

        return status;
    }
}
