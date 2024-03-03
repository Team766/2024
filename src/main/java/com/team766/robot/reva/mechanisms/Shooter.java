package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Map;
import java.util.TreeMap;

public class Shooter extends Mechanism {
    // TODO: fill in appropriate gear ratio
    private static final double GEAR_RATIO = 1.0 * 1.0;
    private static final double DEFAULT_SPEED = 20; // rps
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double MAX_SPEED = 0.8;
    private static final double MIN_SPEED = 0.0;

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    private double shooterSpeed = DEFAULT_SPEED;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_BOTTOM);
    }

    public double getShooterVelocity() {
        return shooterMotorBottom.getSensorVelocity() * GEAR_RATIO;
    }

    public void runShooter() {
        checkContextOwnership();
        shooterMotorTop.set(ControlMode.Velocity, shooterSpeed);
        shooterMotorBottom.set(ControlMode.Velocity, shooterSpeed);
    }

    public void shoot() {
        checkContextOwnership();
        shooterSpeed = DEFAULT_SPEED;
        runShooter();
    }

    public void shootSpeed(double power) {
        checkContextOwnership();
        shooterSpeed = power;
        runShooter();
    }

    public void stop() {
        checkContextOwnership();
        shooterSpeed = 0;
        runShooter();
    }

    public void nudgeUp() {
        shooterSpeed = Math.min(shooterSpeed + NUDGE_INCREMENT, MAX_SPEED);
        runShooter();
    }

    public void nudgeDown() {
        shooterSpeed = Math.max(shooterSpeed - NUDGE_INCREMENT, MIN_SPEED);
        runShooter();
    }

    public void run() {
        SmartDashboard.putNumber("[SHOOTER TARGET SPEED]", shooterSpeed);
        SmartDashboard.putNumber("[SHOOTER ACTUAL SPEED]", getShooterVelocity());
    }
}
