package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    }

    private MotorController leftMotor;
    private MotorController rightMotor;

    private static final double GEAR_RATIO_AND_CIRCUMFERENCE =
            (14. / 50.) * (30. / 42.) * (1.25 * Math.PI);
    private static final double SUPPLY_CURRENT_LIMIT = 30; // max efficiency from spec sheet
    private static final double STATOR_CURRENT_LIMIT = 80; // TUNE THIS!

    private double leftPower = 0;
    private double rightPower = 0;

    public Climber() {
        leftMotor = RobotProvider.instance.getMotor(CLIMBER_LEFT_MOTOR);
        rightMotor = RobotProvider.instance.getMotor(CLIMBER_RIGHT_MOTOR);

        leftMotor.setNeutralMode(NeutralMode.Brake);
        rightMotor.setNeutralMode(NeutralMode.Brake);
        leftMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        rightMotor.setCurrentLimit(SUPPLY_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(leftMotor, STATOR_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(rightMotor, STATOR_CURRENT_LIMIT);
    }

    public void setPower(double power) {
        setLeftPower(power);
        setRightPower(power);
    }

    public void setLeftPower(double power) {
        leftMotor.set(power);
        leftPower = power;
    }

    public void setRightPower(double power) {
        rightMotor.set(power);
        rightPower = power;
    }

    public void stop() {
        stopLeft();
        stopRight();
    }

    public void stopLeft() {
        leftMotor.stopMotor();
        leftPower = 0;
    }

    public void stopRight() {
        rightMotor.stopMotor();
        rightPower = 0;
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

    @Override
    public void run() {
        SmartDashboard.putNumber("[CLIMBER] Left Rotations", leftMotor.getSensorPosition());
        SmartDashboard.putNumber("[CLIMBER] Right Rotations", rightMotor.getSensorPosition());
        SmartDashboard.putNumber("[CLIMBER] Left Height", getHeightLeft());
        SmartDashboard.putNumber("[CLIMBER] Right Height", getHeightRight());
        SmartDashboard.putNumber("[CLIMBER] Left Power", leftPower);
        SmartDashboard.putNumber("[CLIMBER] Right Power", rightPower);
        SmartDashboard.putNumber(
                "[CLIMBER] Left Motor Supply Current", MotorUtil.getCurrentUsage(leftMotor));
        SmartDashboard.putNumber(
                "[CLIMBER] Right Motor Supply Current", MotorUtil.getCurrentUsage(rightMotor));
        SmartDashboard.putNumber(
                "[CLIMBER] Left Motor Stator Current", MotorUtil.getStatorCurrentUsage(leftMotor));
        SmartDashboard.putNumber(
                "[CLIMBER] Right Motor Stator Current",
                MotorUtil.getStatorCurrentUsage(rightMotor));
    }
}
