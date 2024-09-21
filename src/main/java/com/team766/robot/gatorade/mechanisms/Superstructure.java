package com.team766.robot.gatorade.mechanisms;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.framework3.StatusBus.getStatusOrThrow;

import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.robot.gatorade.PlacementPosition;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class Superstructure
        extends Mechanism<
                Superstructure.SuperstructureRequest, Superstructure.SuperstructureStatus> {
    public record SuperstructureStatus() implements Status {}

    public sealed interface SuperstructureRequest extends Request {}

    // NOTE: This request type is private because we don't want to expose the ability
    // to send arbitrary requests to the individual mechanisms.
    private record SimpleRequest(
            Shoulder.ShoulderRequest shoulderRequest,
            Elevator.ElevatorRequest elevatorRequest,
            Wrist.WristRequest wristRequest)
            implements SuperstructureRequest {
        @Override
        public boolean isDone() {
            return shoulderRequest().isDone()
                    && elevatorRequest().isDone()
                    && wristRequest().isDone();
        }
    }

    public SuperstructureRequest makeStop() {
        return new SimpleRequest(new Shoulder.Stop(), new Elevator.Stop(), new Wrist.Stop());
    }

    public SuperstructureRequest makeHoldPosition() {
        return new SimpleRequest(
                Shoulder.makeHoldPosition(), Elevator.makeHoldPosition(), Wrist.makeHoldPosition());
    }

    public static SuperstructureRequest makeNudgeShoulderUp() {
        return new SimpleRequest(
                Shoulder.makeNudgeUp(), Elevator.makeHoldPosition(), Wrist.makeHoldPosition());
    }

    public static SuperstructureRequest makeNudgeShoulderDown() {
        return new SimpleRequest(
                Shoulder.makeNudgeDown(), Elevator.makeHoldPosition(), Wrist.makeHoldPosition());
    }

    public static SuperstructureRequest makeNudgeElevatorUp() {
        return new SimpleRequest(
                Shoulder.makeHoldPosition(), Elevator.makeNudgeUp(), Wrist.makeHoldPosition());
    }

    public static SuperstructureRequest makeNudgeElevatorDown() {
        return new SimpleRequest(
                Shoulder.makeHoldPosition(), Elevator.makeNudgeDown(), Wrist.makeHoldPosition());
    }

    public static SuperstructureRequest makeNudgeWristUp() {
        return new SimpleRequest(
                Shoulder.makeHoldPosition(), Elevator.makeHoldPosition(), Wrist.makeNudgeUp());
    }

    public static SuperstructureRequest makeNudgeWristDown() {
        return new SimpleRequest(
                Shoulder.makeHoldPosition(), Elevator.makeHoldPosition(), Wrist.makeNudgeDown());
    }

    public record MoveToPosition(
            Shoulder.RotateToPosition shoulderSetpoint,
            Elevator.MoveToPosition elevatorSetpoint,
            Wrist.RotateToPosition wristSetpoint)
            implements SuperstructureRequest {
        public static final MoveToPosition RETRACTED =
                new MoveToPosition(
                        Shoulder.RotateToPosition.BOTTOM,
                        Elevator.MoveToPosition.RETRACTED,
                        Wrist.RotateToPosition.RETRACTED);

        public static final MoveToPosition EXTENDED_TO_LOW =
                new MoveToPosition(
                        Shoulder.RotateToPosition.FLOOR,
                        Elevator.MoveToPosition.LOW,
                        Wrist.RotateToPosition.LEVEL);

        public static final MoveToPosition EXTENDED_TO_MID =
                new MoveToPosition(
                        Shoulder.RotateToPosition.RAISED,
                        Elevator.MoveToPosition.MID,
                        Wrist.RotateToPosition.MID_NODE);

        public static final MoveToPosition EXTENDED_TO_HIGH =
                new MoveToPosition(
                        Shoulder.RotateToPosition.RAISED,
                        Elevator.MoveToPosition.HIGH,
                        Wrist.RotateToPosition.HIGH_NODE);

        public static final MoveToPosition EXTENDED_TO_HUMAN_PLAYER_CONE =
                new MoveToPosition(
                        Shoulder.RotateToPosition.RAISED,
                        Elevator.MoveToPosition.HUMAN_CONES,
                        Wrist.RotateToPosition.HUMAN_CONES);

        public static final MoveToPosition EXTENDED_TO_HUMAN_PLAYER_CUBE =
                new MoveToPosition(
                        Shoulder.RotateToPosition.RAISED,
                        Elevator.MoveToPosition.HUMAN_CUBES,
                        Wrist.RotateToPosition.HUMAN_CUBES);

        @Override
        public boolean isDone() {
            return checkForStatusWith(
                            Shoulder.ShoulderStatus.class, s -> s.isNearTo(shoulderSetpoint))
                    && checkForStatusWith(
                            Elevator.ElevatorStatus.class, s -> s.isNearTo(elevatorSetpoint))
                    && checkForStatusWith(Wrist.WristStatus.class, s -> s.isNearTo(wristSetpoint));
        }

        public static MoveToPosition Extended(
                PlacementPosition position, GamePieceType gamePieceType) {
            return switch (position) {
                case NONE -> throw new IllegalArgumentException();
                case HIGH_NODE -> EXTENDED_TO_HIGH;
                case HUMAN_PLAYER -> switch (gamePieceType) {
                    case CONE -> EXTENDED_TO_HUMAN_PLAYER_CONE;
                    case CUBE -> EXTENDED_TO_HUMAN_PLAYER_CUBE;
                };
                case LOW_NODE -> EXTENDED_TO_LOW;
                case MID_NODE -> EXTENDED_TO_MID;
            };
        }
    }

    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;

    public Superstructure() {
        shoulder = new Shoulder();
        shoulder.setSuperstructure(this);
        elevator = new Elevator();
        elevator.setSuperstructure(this);
        wrist = new Wrist();
        wrist.setSuperstructure(this);
    }

    @Override
    protected SuperstructureRequest getInitialRequest() {
        return makeStop();
    }

    protected SuperstructureRequest getIdleRequest() {
        return makeHoldPosition();
    }

    @Override
    protected SuperstructureStatus run(SuperstructureRequest request, boolean isRequestNew) {
        switch (request) {
            case SimpleRequest g -> {
                if (!isRequestNew) break;
                shoulder.setRequest(g.shoulderRequest());
                elevator.setRequest(g.elevatorRequest());
                wrist.setRequest(g.wristRequest());
            }
            case MoveToPosition g -> {
                enum MovePhase {
                    PREPARE_TO_MOVE_ELEVATOR,
                    MOVING_ELEVATOR,
                    ELEVATOR_IN_POSITION
                }

                final boolean raisingShoulder =
                        g.shoulderSetpoint.angle()
                                > getStatusOrThrow(Shoulder.ShoulderStatus.class).angle();

                MovePhase phase;
                if (getStatusOrThrow(Elevator.ElevatorStatus.class).isNearTo(g.elevatorSetpoint)) {
                    phase = MovePhase.ELEVATOR_IN_POSITION;
                } else {
                    phase = MovePhase.MOVING_ELEVATOR;
                    // Always retract the wrist before moving the elevator.
                    // It might already be retracted, so it's possible that this step finishes
                    // instantaneously.
                    if (!getStatusOrThrow(Wrist.WristStatus.class)
                            .isNearTo(Wrist.RotateToPosition.RETRACTED)) {
                        phase = MovePhase.PREPARE_TO_MOVE_ELEVATOR;
                    }
                    // If raising the shoulder, do that before the elevator
                    // (else, lower it after the elevator).
                    if (raisingShoulder
                            && !getStatusOrThrow(Shoulder.ShoulderStatus.class)
                                    .isNearTo(g.shoulderSetpoint)) {
                        phase = MovePhase.PREPARE_TO_MOVE_ELEVATOR;
                    }
                }

                switch (phase) {
                    case PREPARE_TO_MOVE_ELEVATOR -> {
                        shoulder.setRequest(
                                raisingShoulder ? g.shoulderSetpoint : Shoulder.makeHoldPosition());

                        elevator.setRequest(Elevator.makeHoldPosition());

                        wrist.setRequest(Wrist.RotateToPosition.RETRACTED);
                    }
                    case MOVING_ELEVATOR -> {
                        shoulder.setRequest(
                                raisingShoulder ? g.shoulderSetpoint : Shoulder.makeHoldPosition());

                        // Move the elevator until it gets near the target position.
                        elevator.setRequest(g.elevatorSetpoint);

                        wrist.setRequest(Wrist.RotateToPosition.RETRACTED);
                    }
                    case ELEVATOR_IN_POSITION -> {
                        // If lowering the shoulder, do that after the elevator.
                        shoulder.setRequest(g.shoulderSetpoint);

                        elevator.setRequest(g.elevatorSetpoint);

                        // Lastly, move the wrist.
                        wrist.setRequest(g.wristSetpoint);
                    }
                }
            }
        }
        return new SuperstructureStatus();
    }
}
