package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Mechanism {
    private static final double DEFAULT_SPEED =
            2500.0; // motor shaft rps, does not take gearing into account
    private static final double NUDGE_INCREMENT = 100.0;
    private static final double MAX_SPEED = 5600.0; // spec is 6000.0
    private static final double MIN_SPEED = 0.0;
    private static final double SPEED_TOLERANCE = 100.0; // rpm

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    private double targetSpeed = DEFAULT_SPEED;
    private boolean speedUpdated = false;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_BOTTOM);
        shooterMotorTop.setNeutralMode(NeutralMode.Coast);
        shooterMotorBottom.setNeutralMode(NeutralMode.Coast);
    }

    public boolean isCloseToExpectedSpeed() {
        return ((Math.abs(targetSpeed - getShooterSpeedTop()) < SPEED_TOLERANCE)
                && (Math.abs(targetSpeed - getShooterSpeedBottom()) < SPEED_TOLERANCE));
    }

    public boolean isSpinning() {
        return (Math.abs(getShooterSpeedBottom()) + Math.abs(getShooterSpeedTop())) > 50;
    }

    private double getShooterSpeedTop() {
        return shooterMotorTop.getSensorVelocity();
    }

    private double getShooterSpeedBottom() {
        return shooterMotorBottom.getSensorVelocity();
    }

    public void shoot(double speed) {
        checkContextOwnership();
        targetSpeed = com.team766.math.Math.clamp(speed, MIN_SPEED, MAX_SPEED);
        speedUpdated = true;
    }

    public void shoot() {
        shoot(DEFAULT_SPEED);
    }

    public void stop() {
        shoot(0.0);
    }

    public void nudgeUp() {
        shoot(Math.min(targetSpeed + NUDGE_INCREMENT, MAX_SPEED));
    }

    public void nudgeDown() {
        shoot(Math.max(targetSpeed - NUDGE_INCREMENT, MIN_SPEED));
    }

    public void run() {
        SmartDashboard.putNumber("[SHOOTER TARGET SPEED]", targetSpeed);
        SmartDashboard.putNumber("[SHOOTER TOP MOTOR SPEED]", getShooterSpeedTop());
        SmartDashboard.putNumber("[SHOOTER BOTTOM MOTOR SPEED]", getShooterSpeedBottom());

        // FIXME: problem with this - does not pay attention to changes in PID values
        // https://github.com/Team766/2024/pull/49 adds support to address this
        // until then, this is equivalent to the earlier approach
        if (speedUpdated) {
            shooterMotorTop.set(ControlMode.Velocity, targetSpeed);
            shooterMotorBottom.set(ControlMode.Velocity, targetSpeed);
            speedUpdated = false;
        }
    }
}
