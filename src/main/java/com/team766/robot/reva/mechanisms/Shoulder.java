package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_ENCODER;
import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_LEFT;
import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_RIGHT;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.REVThroughBoreDutyCycleEncoder;
import com.team766.library.ValueProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shoulder extends Mechanism {
    public enum ShoulderPosition {
        // TODO: Find actual values.
        BOTTOM(0),
        INTAKE_FLOOR(0),
        SHOOT_LOW(15),
        SHOOTER_ASSIST(18.339),
        SHOOT_MEDIUM(30),
        SHOOT_HIGH(80),
        AMP(90),
        TOP(105); // angle needed to be upped so it works with the climber

        private final double angle;

        ShoulderPosition(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }
    }

    private double targetAngle;
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
    private double targetRotations = 0.0;

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

        absoluteEncoder =
                (REVThroughBoreDutyCycleEncoder)
                        RobotProvider.instance.getEncoder(SHOULDER_ENCODER);
        leftMotor.setSensorPosition(DEFAULT_POSITION);
        targetAngle = -1;
    }

    public void stop() {
        leftMotor.stopMotor();
    }

    public void reset() {
        targetRotations = 0.0;
        leftMotor.setSensorPosition(0.0);
    }

    public void nudgeUp() {
        rotate(getAngle() + NUDGE_AMOUNT);
    }

    public void nudgeDown() {
        rotate(getAngle() - NUDGE_AMOUNT);
    }

    public double getAbsoluteEncoderPosition() {
        return absoluteEncoder.getAbsolutePosition();
    }

    public double getTargetAngle() {
        return targetAngle;
    }

    public double getRotations() {
        return leftMotor.getSensorPosition();
    }

    public double getAngle() {
        return rotationsToDegrees(leftMotor.getSensorPosition());
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

    public void rotate(ShoulderPosition position) {
        rotate(position.getAngle());
    }

    public void rotate(double angle) {
        checkContextOwnership();
        targetAngle =
                com.team766.math.Math.clamp(
                        angle, ShoulderPosition.BOTTOM.getAngle(), ShoulderPosition.TOP.getAngle());
        targetRotations = degreesToRotations(targetAngle);
        // SmartDashboard.putNumber("[SHOULDER Target Angle]", targetAngle);
        // actual rotation will happen in run()
    }

    public boolean isFinished() {
        return Math.abs(getAngle() - targetAngle) < 2.5;
    }

    @Override
    public void run() {
        // encoder takes some time to settle.
        // this threshold was determined very scientifically around 3:20am.
        if (encoderInitializationCount < ENCODER_INITIALIZATION_LOOPS
                && absoluteEncoder.isConnected()) {
            double absPos = absoluteEncoder.getAbsolutePosition() - 0.071;
            double convertedPos = absoluteEncoderToMotorRotations(absPos);
            // TODO: only set the sensor position after this has settled?
            // can try in the next round of testing.
            leftMotor.setSensorPosition(convertedPos);
            encoderInitializationCount++;
        }
        SmartDashboard.putNumber("[SHOULDER] Angle", getAngle());
        SmartDashboard.putNumber("[SHOULDER] Target Angle", targetAngle);
        // SmartDashboard.putNumber("[SHOULDER] Rotations", getRotations());
        // SmartDashboard.putNumber("[SHOULDER] Target Rotations", targetRotations);
        // SmartDashboard.putNumber("[SHOULDER] Encoder Frequency", absoluteEncoder.getFrequency());
        // SmartDashboard.putNumber(
        //         "[SHOULDER] Absolute Encoder Position", getAbsoluteEncoderPosition());
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
        // SmartDashboard.putBoolean("Shoulder at angle", isFinished());

        TalonFX leftTalon = (TalonFX) leftMotor;
        // SmartDashboard.putNumber("[SHOULDER] ffGain", ffGain.get());
        double ff = ffGain.valueOr(0.0) * Math.cos(Math.toRadians(getAngle()));
        // SmartDashboard.putNumber("[SHOULDER] FF", ff);
        // SmartDashboard.putNumber("[SHOULDER VELOCITY]", Math.abs(leftMotor.getSensorVelocity()));
        PositionDutyCycle positionRequest = new PositionDutyCycle(targetRotations);
        positionRequest.FeedForward = ff;
        leftTalon.setControl(positionRequest);
    }
}
