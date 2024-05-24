package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework.Subsystem;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import org.littletonrobotics.junction.AutoLogOutput;

public class Shooter extends Subsystem<Shooter.Status, Shooter.Goal> {
    public record Status(double targetSpeed, double shooterSpeedTop, double shooterSpeedBottom) {
        @AutoLogOutput
        public boolean isCloseToTargetSpeed() {
            return isCloseToSpeed(targetSpeed());
        }

        public boolean isCloseToSpeed(double targetSpeed) {
            return ((Math.abs(targetSpeed - shooterSpeedTop()) < SPEED_TOLERANCE)
                    && (Math.abs(targetSpeed - shooterSpeedBottom()) < SPEED_TOLERANCE));
        }
    }

    public sealed interface Goal {}

    public record Stop() implements Goal {}

    public record NudgeUp() implements Goal {}

    public record NudgeDown() implements Goal {}

    public record ShootAtSpeed(double speed) implements Goal {
        public static final ShootAtSpeed SHOOTER_ASSIST_SPEED = new ShootAtSpeed(4000.0);
    }

    public record Shoot() implements Goal {}

    public static final double DEFAULT_SPEED =
            4800.0; // motor shaft rps, does not take gearing into account
    private static final double NUDGE_INCREMENT = 100.0;
    private static final double CURRENT_LIMIT = 40.0; // needs tuning
    private static final double MAX_SPEED = 5600.0; // spec is 6000.0
    private static final double MIN_SPEED = 0.0;

    // TODO: Get the voltage of the battery and set the speed tolerance propotional to this
    private static final double SPEED_TOLERANCE = 200.0; // rpm

    private MotorController shooterMotorTop;
    private MotorController shooterMotorBottom;
    private double targetSpeed = DEFAULT_SPEED;

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

    @Override
    protected Status updateState() {
        // SmartDashboard.putNumber(
        //         "[SHOOTER] Top Motor Current", MotorUtil.getCurrentUsage(shooterMotorTop));
        // SmartDashboard.putNumber(
        //         "[SHOOTER] Bottom Motor Current",
        //         MotorUtil.getCurrentUsage(shooterMotorBottom));

        return new Status(
                getGoal() instanceof Stop ? 0.0 : targetSpeed,
                shooterMotorTop.getSensorVelocity(),
                shooterMotorBottom.getSensorVelocity());
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        if (!goalChanged) {
            return;
        }

        switch (goal) {
            case ShootAtSpeed g -> {
                targetSpeed = com.team766.math.Math.clamp(g.speed(), MIN_SPEED, MAX_SPEED);
                if (targetSpeed == 0.0) {
                    setGoal(new Stop());
                    break;
                }
                shooterMotorTop.set(ControlMode.Velocity, targetSpeed);
                shooterMotorBottom.set(ControlMode.Velocity, targetSpeed);
            }
            case Stop g -> {
                shooterMotorTop.stopMotor();
                shooterMotorBottom.stopMotor();
            }
            case Shoot g -> setGoal(new ShootAtSpeed(targetSpeed));
            case NudgeUp g -> setGoal(
                    new ShootAtSpeed(Math.min(targetSpeed + NUDGE_INCREMENT, MAX_SPEED)));
            case NudgeDown g -> setGoal(
                    new ShootAtSpeed(Math.max(targetSpeed - NUDGE_INCREMENT, MIN_SPEED)));
        }
    }
}
