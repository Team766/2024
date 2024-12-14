package com.team766.robot.burro_elevator.mechanisms;

import static com.team766.framework.Conditions.checkForStatusWith;
import static com.team766.framework.StatusBus.getStatusOrThrow;

import com.team766.framework.Mechanism;
import com.team766.framework.Request;
import com.team766.framework.Status;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.library.RateLimiter;

public class Elevator extends Mechanism<Elevator.ElevatorRequest, Elevator.ElevatorStatus> {
    public record ElevatorStatus(double position) implements Status {}

    public sealed interface ElevatorRequest extends Request {}

    public record SetPower(double power) implements ElevatorRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record SetPosition(double position) implements ElevatorRequest {
        @Override
        public boolean isDone() {
            // TODO: also consider velocity
            return checkForStatusWith(
                    ElevatorStatus.class,
                    s -> Math.abs(s.position - position) < POSITION_TOLERANCE);
        }
    }

    public static ElevatorRequest makeHoldPosition() {
        final double currentPosition = getStatusOrThrow(ElevatorStatus.class).position();
        return new SetPosition(currentPosition);
    }

    public static ElevatorRequest makeNudgeUp() {
        final double currentPosition = getStatusOrThrow(ElevatorStatus.class).position();
        return new SetPosition(currentPosition + NUDGE_UP_INCREMENT);
    }

    public static ElevatorRequest makeNudgeDown() {
        final double currentPosition = getStatusOrThrow(ElevatorStatus.class).position();
        return new SetPosition(currentPosition - NUDGE_DOWN_INCREMENT);
    }

    private static final double NUDGE_UP_INCREMENT = 1.0; // inches
    private static final double NUDGE_DOWN_INCREMENT = 1.0; // inches

    private static final double POSITION_TOLERANCE = 0.5;

    private static final double MOTOR_ROTATIONS_TO_ELEVATOR_POSITION =
            (0.25 /*chain pitch = distance per tooth*/)
                    * (18. /*teeth per rotation of sprocket*/)
                    * (1. / (3. * 4. * 4.) /*planetary gearbox*/);

    private final CANSparkMaxMotorController motor;
    private final RateLimiter dashboardRateLimiter = new RateLimiter(0.1);

    public Elevator() {
        motor = (CANSparkMaxMotorController) RobotProvider.instance.getMotor("elevator.Motor");
        motor.setSmartCurrentLimit(10, 80, 200);
    }

    @Override
    protected ElevatorRequest getInitialRequest() {
        return new SetPower(0);
    }

    @Override
    protected ElevatorRequest getIdleRequest() {
        return makeHoldPosition();
    }

    @Override
    protected ElevatorStatus run(ElevatorRequest request, boolean isRequestNew) {
        switch (request) {
            case SetPower g -> {
                motor.set(g.power);
            }
            case SetPosition g -> {
                motor.set(ControlMode.Position, g.position / MOTOR_ROTATIONS_TO_ELEVATOR_POSITION);
            }
        }

        return new ElevatorStatus(motor.getSensorPosition() * MOTOR_ROTATIONS_TO_ELEVATOR_POSITION);
    }
}
