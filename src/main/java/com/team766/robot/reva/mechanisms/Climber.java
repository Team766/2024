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

    // Are these only for dubugging?
    private double leftTargetRotations = 0;
    private double rightTargetRotations = 0;

    private static final double GEAR_RATIO_AND_CIRCUMFERENCE =
            (14. / 50.) * (30. / 42.) * (1.25 * Math.PI);
    private static final double NUDGE_INCREMENT = 10; // in cm
    private static final double PIDLESS_NUDGE_INCREMENT = 0.2;
    private static final double SUPPLY_CURRENT_LIMIT = 30.0; // max efficiency from spec sheet
    private static final double STATOR_CURRENT_LIMIT = 80.0; // TUNE THIS!

    private double pidlessPower = 0.0;

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

    public boolean isRunningNoPID() {
        return pidlessPower != 0.0;
    }

    public void goNoPIDUp() {
        goNoPIDUpLeft();
        goNoPIDUpRight();
    }

    public void goNoPIDUpLeft() {
        leftMotor.set(PIDLESS_NUDGE_INCREMENT);
    }

    public void goNoPIDUpRight() {
        rightMotor.set(PIDLESS_NUDGE_INCREMENT);
    }

    public void goNoPIDDown() {
        goNoPIDDownLeft();
        goNoPIDDownRight();
    }

    public void goNoPIDDownLeft() {
        leftMotor.set(-PIDLESS_NUDGE_INCREMENT);
    }

    public void goNoPIDDownRight() {
        rightMotor.set(-PIDLESS_NUDGE_INCREMENT);
    }

    public void stop() {
        pidlessPower = 0.0;
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }

    private double heightToRotations(double height) {
        return height * GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    private double rotationsToHeight(double rotations) {
        return rotations / GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    public void setHeight(ClimberPosition position) {
        setHeight(position.getHeight());
    }

    public void setHeight(double height) {
        setHeightLeft(height);
        setHeightRight(height);
    }

    private double setHeight(MotorController motor, double height) {
        double targetHeight =
                com.team766.math.Math.clamp(
                        height,
                        ClimberPosition.BOTTOM.getHeight(),
                        ClimberPosition.TOP.getHeight());
        double targetRotations = heightToRotations(targetHeight);
        motor.set(MotorController.ControlMode.Position, targetRotations);
        return targetRotations;
    }

    public void setHeightLeft(double height) {
        leftTargetRotations = setHeight(leftMotor, height);
    }

    public void setHeightRight(double height) {
        rightTargetRotations = setHeight(rightMotor, height);
    }

    // TODO: remove
    public double getHeight() {
        return getHeightLeft();
    }

    public double getHeightLeft() {
        return rotationsToHeight(leftMotor.getSensorPosition());
    }

    public double getHeightRight() {
        return rotationsToHeight(rightMotor.getSensorPosition());
    }

    public void nudgeUp() {
        pidlessPower = Math.min(1, pidlessPower + PIDLESS_NUDGE_INCREMENT);
        // setHeight(getHeight() + NUDGE_AMOUNT);
    }

    public void nudgeDown() {
        pidlessPower = Math.max(-1, pidlessPower - PIDLESS_NUDGE_INCREMENT);
        // setHeight(getHeight() - NUDGE_AMOUNT);
    }

    @Override
    public void run() {
        SmartDashboard.putNumber("[CLIMBER] Left Rotations", leftMotor.getSensorPosition());
        SmartDashboard.putNumber("[CLIMBER] Right Rotations", rightMotor.getSensorPosition());
        SmartDashboard.putNumber("[CLIMBER] Left Target Rotations", leftTargetRotations);
        SmartDashboard.putNumber("[CLIMBER] Right Target Rotations", rightTargetRotations);
        SmartDashboard.putNumber("[CLIMBER] Height", getHeight());
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
