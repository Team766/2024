package com.team766.robot.burro_arm.mechanisms;

import static com.team766.framework.Conditions.checkForStatusWith;
import static com.team766.framework.StatusBus.getStatusOrThrow;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.framework.Request;
import com.team766.framework.Status;
import com.team766.hal.EncoderReader;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm extends Mechanism<Arm.ArmRequest, Arm.ArmStatus> {
    public record ArmStatus(double angle) implements Status {}

    public sealed interface ArmRequest extends Request {}

    public record SetPower(double power) implements ArmRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record SetAngle(double angle) implements ArmRequest {
        @Override
        public boolean isDone() {
            // TODO: also consider velocity
            return checkForStatusWith(
                    ArmStatus.class, s -> Math.abs(s.angle - angle) < ANGLE_TOLERANCE);
        }
    }

    public static ArmRequest makeHoldPosition() {
        final double currentAngle = getStatusOrThrow(ArmStatus.class).angle();
        return new SetAngle(currentAngle);
    }

    public static ArmRequest makeNudgeUp() {
        final double currentAngle = getStatusOrThrow(ArmStatus.class).angle();
        return new SetAngle(currentAngle + NUDGE_UP_INCREMENT);
    }

    public static ArmRequest makeNudgeDown() {
        final double currentAngle = getStatusOrThrow(ArmStatus.class).angle();
        return new SetAngle(currentAngle - NUDGE_DOWN_INCREMENT);
    }

    private static final double NUDGE_UP_INCREMENT = 5.0; // degrees
    private static final double NUDGE_DOWN_INCREMENT = 5.0; // degrees

    private static final double ANGLE_TOLERANCE = 3; // degrees

    private static final double ABSOLUTE_ENCODER_TO_ARM_ANGLE =
            (360. /*degrees per rotation*/) * (12. / 54. /*chain reduction*/);
    private static final double MOTOR_ROTATIONS_TO_ARM_ANGLE =
            ABSOLUTE_ENCODER_TO_ARM_ANGLE * (1. / (5. * 5. * 5.) /*planetary gearbox*/);

    private final CANSparkMaxMotorController motor;
    private final EncoderReader absoluteEncoder;

    private final ValueProvider<Double> absoluteEncoderOffset;
    private final RateLimiter dashboardRateLimiter = new RateLimiter(0.1);

    private boolean initialized = false;

    public Arm() {
        motor = (CANSparkMaxMotorController) RobotProvider.instance.getMotor("arm.Motor");
        motor.setSmartCurrentLimit(5, 80, 200);
        absoluteEncoder = RobotProvider.instance.getEncoder("arm.AbsoluteEncoder");
        absoluteEncoderOffset = ConfigFileReader.instance.getDouble("arm.AbsoluteEncoderOffset");
    }

    @Override
    protected ArmRequest getInitialRequest() {
        return new SetPower(0);
    }

    @Override
    protected ArmRequest getIdleRequest() {
        return makeHoldPosition();
    }

    @Override
    public ArmStatus run(ArmRequest request, boolean isRequestNew) {
        if (!initialized && absoluteEncoder.isConnected()) {
            final double absoluteEncoderPosition =
                    Math.IEEEremainder(
                            absoluteEncoder.getPosition() + absoluteEncoderOffset.get(), 1.0);
            SmartDashboard.putNumber(
                    "[ARM] AbsoluteEncoder Init Position", absoluteEncoderPosition);
            motor.setSensorPosition(
                    absoluteEncoderPosition
                            * ABSOLUTE_ENCODER_TO_ARM_ANGLE
                            / MOTOR_ROTATIONS_TO_ARM_ANGLE);
            initialized = true;
        }

        final ArmStatus status =
                new ArmStatus(motor.getSensorPosition() * MOTOR_ROTATIONS_TO_ARM_ANGLE);

        switch (request) {
            case SetPower g -> {
                motor.set(g.power);
            }
            case SetAngle g -> {
                motor.set(ControlMode.Position, g.angle / MOTOR_ROTATIONS_TO_ARM_ANGLE);
            }
        }

        return status;
    }
}
