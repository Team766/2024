package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.library.ValueProvider;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Mechanism {
    private static final double DEFAULT_SPEED = 20.0; // rps
    private static final double NUDGE_INCREMENT = 0.8;
    private static final double MAX_SPEED = 112.5;
    private static final double MIN_SPEED = 0.0;
    private static final double SPEED_TOLERANCE = 0.75; // rps

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    private double targetShooterSpeed = DEFAULT_SPEED;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_BOTTOM);
    }

    public boolean isCloseToExpectedSpeed() {
        return (Math.abs(targetShooterSpeed - getShooterSpeed()) < SPEED_TOLERANCE);
    }

    public double getShooterSpeed() {
        return shooterMotorBottom.getSensorVelocity();
    }

    public void runShooter() {
        checkContextOwnership();
        shooterMotorTop.set(ControlMode.Velocity, targetShooterSpeed);
        shooterMotorBottom.set(ControlMode.Velocity, targetShooterSpeed);
    }

    public void shoot() {
        checkContextOwnership();
        targetShooterSpeed = DEFAULT_SPEED;
        runShooter();
    }

    public void shootSpeed(double speed) {
        checkContextOwnership();
        targetShooterSpeed = speed;
        runShooter();
    }

    public void stop() {
        checkContextOwnership();
        targetShooterSpeed = 0;
        runShooter();
    }

    public void nudgeUp() {
        targetShooterSpeed = Math.min(targetShooterSpeed + NUDGE_INCREMENT, MAX_SPEED);
        runShooter();
    }

    public void nudgeDown() {
        targetShooterSpeed = Math.max(targetShooterSpeed - NUDGE_INCREMENT, MIN_SPEED);
        runShooter();
    }

    public void run() {
        SmartDashboard.putNumber("[SHOOTER TARGET SPEED]", targetShooterSpeed);
        SmartDashboard.putNumber("[SHOOTER ACTUAL SPEED]", getShooterSpeed());
    }
}
