package com.team766.robot.reva.mechanisms;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.framework3.StatusBus.getStatusOrThrow;
import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkMax;
import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;

public class Shooter extends Mechanism<Shooter.ShooterRequest, Shooter.ShooterStatus> {
    public record ShooterStatus(
            double targetSpeed, double shooterSpeedTop, double shooterSpeedBottom)
            implements Status {
        public boolean isCloseToTargetSpeed() {
            return isCloseToSpeed(targetSpeed());
        }

        public boolean isCloseToSpeed(double targetSpeed) {
            return ((Math.abs(targetSpeed - shooterSpeedTop()) < SPEED_TOLERANCE)
                    && (Math.abs(targetSpeed - shooterSpeedBottom()) < SPEED_TOLERANCE));
        }
    }

    public sealed interface ShooterRequest extends Request {}

    public record Stop() implements ShooterRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public static ShooterRequest makeNudgeUp() {
        final double previousTarget = getStatusOrThrow(ShooterStatus.class).targetSpeed();
        return new ShootAtSpeed(Math.min(previousTarget + NUDGE_INCREMENT, MAX_SPEED));
    }

    public static ShooterRequest makeNudgeDown() {
        final double previousTarget = getStatusOrThrow(ShooterStatus.class).targetSpeed();
        return new ShootAtSpeed(Math.max(previousTarget - NUDGE_INCREMENT, MIN_SPEED));
    }

    public record ShootAtSpeed(double speed) implements ShooterRequest {
        public static final ShootAtSpeed SHOOTER_ASSIST_SPEED = new ShootAtSpeed(4000.0);

        @Override
        public boolean isDone() {
            return checkForStatusWith(ShooterStatus.class, s -> s.isCloseToSpeed(this.speed()));
        }
    }

    public static ShooterRequest makeShoot() {
        final double previousTarget = getStatusOrThrow(ShooterStatus.class).targetSpeed();
        return new ShootAtSpeed(previousTarget);
    }

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
    protected ShooterRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected ShooterRequest getIdleRequest() {
        return new Stop();
    }

    @Override
    protected ShooterStatus run(ShooterRequest request, boolean isRequestNew) {
        if (isRequestNew) {
            switch (request) {
                case ShootAtSpeed g -> {
                    targetSpeed = com.team766.math.Math.clamp(g.speed(), MIN_SPEED, MAX_SPEED);
                    if (targetSpeed == 0.0) {
                        setRequest(new Stop());
                        break;
                    }
                    shooterMotorTop.set(ControlMode.Velocity, targetSpeed);
                    shooterMotorBottom.set(ControlMode.Velocity, targetSpeed);
                }
                case Stop g -> {
                    shooterMotorTop.stopMotor();
                    shooterMotorBottom.stopMotor();
                }
            }
        }

        // SmartDashboard.putNumber(
        //         "[SHOOTER] Top Motor Current", MotorUtil.getCurrentUsage(shooterMotorTop));
        // SmartDashboard.putNumber(
        //         "[SHOOTER] Bottom Motor Current",
        //         MotorUtil.getCurrentUsage(shooterMotorBottom));

        return new ShooterStatus(
                targetSpeed,
                shooterMotorTop.getSensorVelocity(),
                shooterMotorBottom.getSensorVelocity());
    }
}
