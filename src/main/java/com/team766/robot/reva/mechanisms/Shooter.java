package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Mechanism {
    private static final double DEFAULT_POWER = 0.25;
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double MAX_POWER = 0.5;
    private static final double MIN_POWER = 0;

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    private double shooterPower = 0;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
    }

    private void runShooter() {
        checkContextOwnership();
        shooterMotorTop.set(shooterPower);
        shooterMotorBottom.set(shooterPower);
    }

    public void shoot() {
        checkContextOwnership();
        shooterPower = DEFAULT_POWER;
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
    }
}
