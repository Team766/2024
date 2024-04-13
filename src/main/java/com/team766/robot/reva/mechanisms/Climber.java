package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Climber extends Mechanism {

    public enum ClimberPosition {
        // A very rough measurement, and was being very safe.
        // TODO: Needs to be measured more accurately.
        TOP(43.18),
        BOTTOM(0);

        private final double height;

        private ClimberPosition(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }

        public double getRotations() {
            return heightToRotations(height);
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

    private double leftPower = 0;
    private double rightPower = 0;

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
    }

    public void enableSoftLimits(boolean enabled) {
        MotorUtil.enableSoftLimits(leftMotor, enabled);
        MotorUtil.enableSoftLimits(rightMotor, enabled);
    }

    public void resetLeftPosition() {
        leftMotor.setSensorPosition(0);
    }

    public void resetRightPosition() {
        rightMotor.setSensorPosition(0);
    }

    public void setPower(double power) {
        setLeftPower(power);
        setRightPower(power);
    }

    public void setLeftPower(double power) {
        power = com.team766.math.Math.clamp(power, -1, 1);
        leftPower = power;
        leftMotor.set(power);
    }

    public void setRightPower(double power) {
        power = com.team766.math.Math.clamp(power, -1, 1);
        rightPower = power;
        rightMotor.set(power);
    }

    public void stop() {
        stopLeft();
        stopRight();
    }

    public void stopLeft() {
        leftPower = 0;
        leftMotor.stopMotor();
    }

    public void stopRight() {
        rightPower = 0;
        rightMotor.stopMotor();
    }

    private static double heightToRotations(double height) {
        return height * GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    private static double rotationsToHeight(double rotations) {
        return rotations / GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    public double getHeightLeft() {
        return rotationsToHeight(leftMotor.getSensorPosition());
    }

    public double getHeightRight() {
        return rotationsToHeight(rightMotor.getSensorPosition());
    }

    public boolean isLeftAtBottom() {
        return Math.abs(getHeightLeft()) < POSITION_LOCATION_THRESHOLD;
    }

    public boolean isRightAtBottom() {
        return Math.abs(getHeightRight()) < POSITION_LOCATION_THRESHOLD;
    }

    @Override
    public void run() {
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
    }
}
