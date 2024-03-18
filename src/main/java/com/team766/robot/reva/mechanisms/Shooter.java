package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Mechanism {
    private static final double DEFAULT_SPEED =
            4500.0; // motor shaft rps, does not take gearing into account
    private static final double NUDGE_INCREMENT = 100.0;
    private static final double CURRENT_LIMIT = 40.0; // needs tuning
    private static final double MAX_SPEED = 5600.0; // spec is 6000.0
    private static final double MIN_SPEED = 0.0;
    private static final double SPEED_TOLERANCE = 400.0; // rpm

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    // decrease period if we're tuning PID
    private RateLimiter rateLimiter = new RateLimiter(10.0);
    private boolean shouldRun = false;
    // only used if shouldRun is true
    private double targetSpeed = DEFAULT_SPEED;
    private boolean speedUpdated = false;

    public Shooter() {
        shooterMotorTop = RobotProvider.instance.getMotor(SHOOTER_MOTOR_TOP);
        shooterMotorBottom = RobotProvider.instance.getMotor(SHOOTER_MOTOR_BOTTOM);
        CANSparkMax canTop = (CANSparkMax) shooterMotorTop;
        CANSparkMax canBottom = (CANSparkMax) shooterMotorBottom;
        canTop.enableVoltageCompensation(12.0);
        canBottom.enableVoltageCompensation(12.0);

        shooterMotorTop.setNeutralMode(NeutralMode.Coast);
        shooterMotorBottom.setNeutralMode(NeutralMode.Coast);
        shooterMotorTop.setCurrentLimit(CURRENT_LIMIT);
        shooterMotorBottom.setCurrentLimit(CURRENT_LIMIT);
    }

    public boolean isCloseToExpectedSpeed() {
        return ((Math.abs(targetSpeed - getShooterSpeedTop()) < SPEED_TOLERANCE)
                && (Math.abs(targetSpeed - getShooterSpeedBottom()) < SPEED_TOLERANCE));
    }

    private double getShooterSpeedTop() {
        return shooterMotorTop.getSensorVelocity();
    }

    private double getShooterSpeedBottom() {
        return shooterMotorBottom.getSensorVelocity();
    }

    public void shoot(double speed) {
        targetSpeed = com.team766.math.Math.clamp(speed, MIN_SPEED, MAX_SPEED);
        shoot();
    }

    public void shoot() {
        checkContextOwnership();
        shouldRun = targetSpeed > 0.0;
        speedUpdated = true;
    }

    public void stop() {
        shouldRun = false;
        speedUpdated = true;
    }

    public void nudgeUp() {
        shoot(Math.min(targetSpeed + NUDGE_INCREMENT, MAX_SPEED));
    }

    public void nudgeDown() {
        shoot(Math.max(targetSpeed - NUDGE_INCREMENT, MIN_SPEED));
    }

    public void run() {
        if (speedUpdated || rateLimiter.next()) {
            SmartDashboard.putNumber("[SHOOTER TARGET SPEED]", shouldRun ? targetSpeed : 0.0);
            SmartDashboard.putNumber("[SHOOTER TOP MOTOR SPEED]", getShooterSpeedTop());
            SmartDashboard.putNumber("[SHOOTER BOTTOM MOTOR SPEED]", getShooterSpeedBottom());
            SmartDashboard.putNumber(
                    "[SHOOTER] Top Motor Current", MotorUtil.getCurrentUsage(shooterMotorTop));
            SmartDashboard.putNumber(
                    "[SHOOTER] Bottom Motor Current",
                    MotorUtil.getCurrentUsage(shooterMotorBottom));
        }

        // FIXME: problem with this - does not pay attention to changes in PID values
        // https://github.com/Team766/2024/pull/49 adds support to address this
        // until then, this is equivalent to the earlier approach
        if (speedUpdated) {
            if (shouldRun) {
                shooterMotorTop.set(ControlMode.Velocity, targetSpeed);
                shooterMotorBottom.set(ControlMode.Velocity, targetSpeed);
            } else {
                shooterMotorTop.stopMotor();
                shooterMotorBottom.stopMotor();
            }
            speedUpdated = false;
        }
    }
}
