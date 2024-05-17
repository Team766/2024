package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_ENCODER;
import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_LEFT;
import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_RIGHT;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Subsystem;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.REVThroughBoreDutyCycleEncoder;
import com.team766.library.ValueProvider;
import org.littletonrobotics.junction.AutoLogOutput;

public class Shoulder extends Subsystem<Shoulder.Status, Shoulder.Goal> {
    public record Status(@AutoLogOutput double angle) {
        public boolean isNearTo(RotateToPosition target) {
            return isNearTo(target.angle());
        }

        public boolean isNearTo(double targetAngle) {
            return Math.abs(angle() - targetAngle) < 2.5;
        }
    }

    public sealed interface Goal {}

    public record Stop() implements Goal {}

    public record NudgeUp() implements Goal {}

    public record NudgeDown() implements Goal {}

    public record RotateToPosition(double angle) implements Goal {
        // TODO: Find actual values.
        public static final RotateToPosition BOTTOM = new RotateToPosition(0);
        public static final RotateToPosition INTAKE_FLOOR = new RotateToPosition(0);
        public static final RotateToPosition SHOOT_LOW = new RotateToPosition(15);
        public static final RotateToPosition SHOOTER_ASSIST = new RotateToPosition(18.39);
        public static final RotateToPosition SHOOT_MEDIUM = new RotateToPosition(30);
        public static final RotateToPosition SHOOT_HIGH = new RotateToPosition(80);
        public static final RotateToPosition AMP = new RotateToPosition(90);
        public static final RotateToPosition TOP =
                new RotateToPosition(105); // angle needed to be upped so it works with the climber
    }

    private static final double NUDGE_AMOUNT = 1; // degrees
    private static final double ENCODER_INITIALIZATION_LOOPS = 350;

    private final REVThroughBoreDutyCycleEncoder absoluteEncoder;
    private int encoderInitializationCount = 0;
    private static final double SUPPLY_CURRENT_LIMIT = 30.0; // max efficiency from spec sheet
    private static final double STATOR_CURRENT_LIMIT = 80.0; // TUNE THIS!
    private static final double DEFAULT_POSITION = 77.0;

    private MotorController leftMotor;
    private MotorController rightMotor;

    private ValueProvider<Double> ffGain;

    public Shoulder() {
        // TODO: Initialize and use CANCoders to get offset for relative encoder on boot.
        leftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT);
        rightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT);
        rightMotor.follow(leftMotor);

        leftMotor.setNeutralMode(NeutralMode.Brake);
        rightMotor.setNeutralMode(NeutralMode.Brake);
        leftMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        rightMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(leftMotor, STATOR_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(rightMotor, STATOR_CURRENT_LIMIT);

        ffGain = ConfigFileReader.getInstance().getDouble("shoulder.leftMotor.ffGain");

        absoluteEncoder = (REVThroughBoreDutyCycleEncoder)
                RobotProvider.instance.getEncoder(SHOULDER_ENCODER);
        leftMotor.setSensorPosition(DEFAULT_POSITION);
    }

    public void reset() {
        leftMotor.setSensorPosition(0.0);
        setGoal(new Stop());
    }

    @Override
    protected Status updateState() {
        // encoder takes some time to settle.
        // this threshold was determined very scientifically around 3:20am.
        final double absPos = absoluteEncoder.getAbsolutePosition();
        if (encoderInitializationCount < ENCODER_INITIALIZATION_LOOPS
                && absoluteEncoder.isConnected()) {
            double convertedPos = absoluteEncoderToMotorRotations(absPos - 0.071);
            // TODO: only set the sensor position after this has settled?
            // can try in the next round of testing.
            leftMotor.setSensorPosition(convertedPos);
            encoderInitializationCount++;
        }
        final double rotations = leftMotor.getSensorPosition();
        final double angle = rotationsToDegrees(rotations);
        // SmartDashboard.putNumber("[SHOULDER] Rotations", rotations);
        // SmartDashboard.putNumber("[SHOULDER] Encoder Frequency", absoluteEncoder.getFrequency());
        // SmartDashboard.putNumber("[SHOULDER] Absolute Encoder Position", absPos);
        // SmartDashboard.putNumber(
        //         "[SHOULDER] Left Motor Supply Current", MotorUtil.getCurrentUsage(leftMotor));
        // SmartDashboard.putNumber(
        //         "[SHOULDER] Right Motor Supply Current", MotorUtil.getCurrentUsage(rightMotor));
        // SmartDashboard.putNumber(
        //         "[SHOULDER] Left Motor Stator Current",
        // MotorUtil.getStatorCurrentUsage(leftMotor));
        // SmartDashboard.putNumber(
        //         "[SHOULDER] Right Motor Stator Current",
        //         MotorUtil.getStatorCurrentUsage(rightMotor));
        // SmartDashboard.putNumber("[SHOULDER VELOCITY]", Math.abs(leftMotor.getSensorVelocity()));

        return new Status(angle);
    }

    private double degreesToRotations(double angle) {
        // angle * sprocket ratio * net gear ratio * (rotations / degrees)
        return angle * (54. / 15.) * (4. / 1.) * (3. / 1.) * (3. / 1.) * (1. / 360.);
    }

    private double rotationsToDegrees(double rotations) {
        // angle * sprocket ratio * net gear ratio * (degrees / rotations)
        return rotations * (15. / 54.) * (1. / 4.) * (1. / 3.) * (1. / 3.) * (360. / 1.);
    }

    private double absoluteEncoderToMotorRotations(double rotations) {
        return ((1.05 - rotations) % 1.0 - 0.05) * (4. / 1.) * (3. / 1.) * (3. / 1.);
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        switch (goal) {
            case Stop g -> {
                if (!goalChanged) return;
                leftMotor.stopMotor();
            }
            case NudgeUp g -> {
                setGoal(new RotateToPosition(status.angle() + NUDGE_AMOUNT));
            }
            case NudgeDown g -> {
                setGoal(new RotateToPosition(status.angle() - NUDGE_AMOUNT));
            }
            case RotateToPosition g -> {
                final double targetAngle = com.team766.math.Math.clamp(
                        g.angle(), RotateToPosition.BOTTOM.angle(), RotateToPosition.TOP.angle());
                final double targetRotations = degreesToRotations(targetAngle);

                // SmartDashboard.putBoolean("Shoulder at angle", status.isNearTo(g));

                TalonFX leftTalon = (TalonFX) leftMotor;
                // SmartDashboard.putNumber("[SHOULDER] ffGain", ffGain.get());
                double ff = ffGain.valueOr(0.0) * Math.cos(Math.toRadians(status.angle()));
                // SmartDashboard.putNumber("[SHOULDER] FF", ff);
                PositionDutyCycle positionRequest = new PositionDutyCycle(targetRotations);
                positionRequest.FeedForward = ff;
                leftTalon.setControl(positionRequest);
            }
        }
    }
}
