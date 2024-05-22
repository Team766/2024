package com.team766.robot.gatorade.mechanisms;

import com.team766.framework.Subsystem;
import com.team766.robot.gatorade.PlacementPosition;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class Superstructure extends Subsystem<Superstructure.State, Superstructure.Goal> {
    public record State(Shoulder.Status shoulder, Elevator.Status elevator, Wrist.Status wrist) {
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

    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;

    public Superstructure(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
    }

    @Override
    protected State updateState() {
        return new State(shoulder.getStatus(), elevator.getStatus(), wrist.getStatus());
    }

    @Override
    protected void dispatch(State status, Goal goal, boolean goalChanged) {
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
                // If raising the shoulder, do that before the elevator;
                // else, lower it after the elevator.
                boolean raisingShoulder =
                        g.shoulderSetpoint.angle() > shoulder.getStatus().angle();

                if (!elevator.getStatus().isNearTo(g.elevatorSetpoint)) {
                    // Always retract the wrist before moving the elevator.
                    // It might already be retracted, so it's possible that this step finishes
                    // instantaneously.
                    wrist.setGoal(Wrist.RotateToPosition.RETRACTED);
                    if (raisingShoulder) {
                        shoulder.setGoal(g.shoulderSetpoint);
                    } else {
                        shoulder.setGoal(new Shoulder.HoldPosition());
                    }
                    if (wrist.getStatus().isNearTo(Wrist.RotateToPosition.RETRACTED)
                            && (!raisingShoulder
                                    || shoulder.getStatus().isNearTo(g.shoulderSetpoint))) {
                        // Move the elevator until it gets near the target position.
                        elevator.setGoal(g.elevatorSetpoint);
                    } else {
                        elevator.setGoal(new Elevator.HoldPosition());
                    }
                } else {
                    elevator.setGoal(g.elevatorSetpoint);

                    // If lowering the shoulder, do that after the elevator.
                    shoulder.setGoal(g.shoulderSetpoint);

                    // Lastly, move the wrist.
                    wrist.setGoal(g.wristSetpoint);
                }
            }
        }
    }
}
