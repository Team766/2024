package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Map;
import java.util.TreeMap;

public class Shooter extends Mechanism {

    private static final double DEFAULT_POWER = 0.75;
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double MAX_POWER = 0.8;
    private static final double MIN_POWER = 0.0;
    private static final double SPEED_TOLERANCE = 5.0; // rps

    private final MotorController shooterMotorBottom;
    private final MotorController shooterMotorTop;
    private double shooterPower = DEFAULT_POWER;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_BOTTOM);
    }

    public double getCurrentSpeed() {
        return shooterMotorTop.getSensorVelocity();
    }

    public boolean isCloseToExpectedSpeed() {
        double expectedSpeed = 0.0;
        return (Math.abs(getCurrentSpeed() - expectedSpeed) < SPEED_TOLERANCE);
    }

    public void runShooter() {
        checkContextOwnership();
        shooterMotorTop.set(shooterPower);
        shooterMotorBottom.set(shooterPower);
    }

    public void shoot() {
        checkContextOwnership();
        shootPower(DEFAULT_POWER);
    }

    public void shootPower(double power) {
        checkContextOwnership();
        shooterPower = power;
        runShooter();
    }

    public void stop() {
        checkContextOwnership();
        shooterPower = 0;
        runShooter();
    }

    public void nudgeUp() {
        shooterPower = Math.min(shooterPower + NUDGE_INCREMENT, MAX_POWER);
        runShooter();
    }

    public void nudgeDown() {
        shooterPower = Math.max(shooterPower - NUDGE_INCREMENT, MIN_POWER);
        runShooter();
    }

    public void run() {
        SmartDashboard.putNumber("[SHOOTER POWER]", shooterPower);
        SmartDashboard.putNumber("[SHOOTER SPEED]", getCurrentSpeed());
    }
}
