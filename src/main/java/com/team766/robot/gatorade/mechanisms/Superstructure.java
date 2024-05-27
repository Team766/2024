package com.team766.robot.gatorade.mechanisms;

import com.team766.framework.RobotSystem;
import com.team766.robot.gatorade.PlacementPosition;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class Superstructure extends RobotSystem<Superstructure.Status, Superstructure.Goal> {
    public record Status(Shoulder.Status shoulder, Elevator.Status elevator, Wrist.Status wrist) {
        public boolean isNearTo(MoveToPosition position) {
            return shoulder.isNearTo(position.shoulderSetpoint)
                    && elevator.isNearTo(position.elevatorSetpoint)
                    && wrist.isNearTo(position.wristSetpoint);
        }
    }

    public sealed interface Goal {}

    public record Stop() implements Goal {}

    public record HoldPosition() implements Goal {}

    // NOTE: These Nudge* record types are private because we don't want to expose the ability
    // to send arbitrary goals to the individual mechanisms.
    private record NudgeShoulder(Shoulder.Goal shoulderGoal) implements Goal {}

    public static final NudgeShoulder NUDGE_SHOULDER_UP = new NudgeShoulder(new Shoulder.NudgeUp());
    public static final NudgeShoulder NUDGE_SHOULDER_DOWN =
            new NudgeShoulder(new Shoulder.NudgeDown());

    private record NudgeElevator(Elevator.Goal elevatorGoal) implements Goal {}

    public static final NudgeElevator NUDGE_ELEVATOR_UP = new NudgeElevator(new Elevator.NudgeUp());
    public static final NudgeElevator NUDGE_ELEVATOR_DOWN =
            new NudgeElevator(new Elevator.NudgeDown());

    private record NudgeWrist(Wrist.Goal wristGoal) implements Goal {}

    public static final NudgeWrist NUDGE_WRIST_UP = new NudgeWrist(new Wrist.NudgeUp());
    public static final NudgeWrist NUDGE_WRIST_DOWN = new NudgeWrist(new Wrist.NudgeDown());

    public record MoveToPosition(
            Shoulder.RotateToPosition shoulderSetpoint,
            Elevator.MoveToPosition elevatorSetpoint,
            Wrist.RotateToPosition wristSetpoint)
            implements Goal {
        public static final MoveToPosition RETRACTED = new MoveToPosition(
                Shoulder.RotateToPosition.BOTTOM,
                Elevator.MoveToPosition.RETRACTED,
                Wrist.RotateToPosition.RETRACTED);

        public static final MoveToPosition EXTENDED_TO_LOW = new MoveToPosition(
                Shoulder.RotateToPosition.FLOOR,
                Elevator.MoveToPosition.LOW,
                Wrist.RotateToPosition.LEVEL);

        public static final MoveToPosition EXTENDED_TO_MID = new MoveToPosition(
                Shoulder.RotateToPosition.RAISED,
                Elevator.MoveToPosition.MID,
                Wrist.RotateToPosition.MID_NODE);

        public static final MoveToPosition EXTENDED_TO_HIGH = new MoveToPosition(
                Shoulder.RotateToPosition.RAISED,
                Elevator.MoveToPosition.HIGH,
                Wrist.RotateToPosition.HIGH_NODE);

        public static final MoveToPosition EXTENDED_TO_HUMAN_PLAYER_CONE = new MoveToPosition(
                Shoulder.RotateToPosition.RAISED,
                Elevator.MoveToPosition.HUMAN_CONES,
                Wrist.RotateToPosition.HUMAN_CONES);

        public static final MoveToPosition EXTENDED_TO_HUMAN_PLAYER_CUBE = new MoveToPosition(
                Shoulder.RotateToPosition.RAISED,
                Elevator.MoveToPosition.HUMAN_CUBES,
                Wrist.RotateToPosition.HUMAN_CUBES);

        public static MoveToPosition Extended(
                PlacementPosition position, GamePieceType gamePieceType) {
            return switch (position) {
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

    private final Shoulder shoulder = new Shoulder();
    private final Elevator elevator = new Elevator();
    private final Wrist wrist = new Wrist();

    @Override
    protected Status updateState() {
        return new Status(shoulder.updateState(), elevator.updateState(), wrist.updateState());
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        switch (goal) {
            case Stop g -> {
                if (!goalChanged) break;
                shoulder.setGoal(new Shoulder.Stop());
                elevator.setGoal(new Elevator.Stop());
                wrist.setGoal(new Wrist.Stop());
            }
            case HoldPosition g -> {
                if (!goalChanged) break;
                shoulder.setGoal(new Shoulder.HoldPosition());
                elevator.setGoal(new Elevator.HoldPosition());
                wrist.setGoal(new Wrist.HoldPosition());
            }
            case NudgeShoulder g -> {
                if (!goalChanged) break;
                shoulder.setGoal(g.shoulderGoal());
                elevator.setGoal(new Elevator.HoldPosition());
                wrist.setGoal(new Wrist.HoldPosition());
            }
            case NudgeElevator g -> {
                if (!goalChanged) break;
                shoulder.setGoal(new Shoulder.HoldPosition());
                elevator.setGoal(g.elevatorGoal());
                wrist.setGoal(new Wrist.HoldPosition());
            }
            case NudgeWrist g -> {
                if (!goalChanged) break;
                shoulder.setGoal(new Shoulder.HoldPosition());
                elevator.setGoal(new Elevator.HoldPosition());
                wrist.setGoal(g.wristGoal());
            }
            case MoveToPosition g -> {
                enum MovePhase {
                    PREPARE_TO_MOVE_ELEVATOR,
                    MOVING_ELEVATOR,
                    ELEVATOR_IN_POSITION
                }

                final boolean raisingShoulder =
                        g.shoulderSetpoint.angle() > shoulder.getStatus().angle();

                MovePhase phase;
                if (elevator.getStatus().isNearTo(g.elevatorSetpoint)) {
                    phase = MovePhase.ELEVATOR_IN_POSITION;
                } else {
                    phase = MovePhase.MOVING_ELEVATOR;
                    // Always retract the wrist before moving the elevator.
                    // It might already be retracted, so it's possible that this step finishes
                    // instantaneously.
                    if (!wrist.getStatus().isNearTo(Wrist.RotateToPosition.RETRACTED)) {
                        phase = MovePhase.PREPARE_TO_MOVE_ELEVATOR;
                    }
                    // If raising the shoulder, do that before the elevator
                    // (else, lower it after the elevator).
                    if (raisingShoulder && !shoulder.getStatus().isNearTo(g.shoulderSetpoint)) {
                        phase = MovePhase.PREPARE_TO_MOVE_ELEVATOR;
                    }
                }

                switch (phase) {
                    case PREPARE_TO_MOVE_ELEVATOR -> {
                        shoulder.setGoal(
                                raisingShoulder ? g.shoulderSetpoint : new Shoulder.HoldPosition());

                        elevator.setGoal(new Elevator.HoldPosition());

                        wrist.setGoal(Wrist.RotateToPosition.RETRACTED);
                    }
                    case MOVING_ELEVATOR -> {
                        shoulder.setGoal(
                                raisingShoulder ? g.shoulderSetpoint : new Shoulder.HoldPosition());

                        // Move the elevator until it gets near the target position.
                        elevator.setGoal(g.elevatorSetpoint);

                        wrist.setGoal(Wrist.RotateToPosition.RETRACTED);
                    }
                    case ELEVATOR_IN_POSITION -> {
                        // If lowering the shoulder, do that after the elevator.
                        shoulder.setGoal(g.shoulderSetpoint);

                        elevator.setGoal(g.elevatorSetpoint);

                        // Lastly, move the wrist.
                        wrist.setGoal(g.wristSetpoint);
                    }
                }
            }
        }
    }
}
