package com.team766.robot.reva.mechanisms;

import com.team766.framework.RobotSystem;

public class Superstructure extends RobotSystem<Superstructure.Status, Superstructure.Goal> {

    public record Status() {}

    public sealed interface Goal {}

    public record ShoulderGoal(Shoulder.Goal shoulderGoal) implements Goal {}

    public record ClimberGoal(Climber.Goal climberGoal) implements Goal {}

    private final Shoulder shoulder = new Shoulder();
    private final Climber climber = new Climber();

    public void setGoal(Shoulder.Goal shoulderGoal) {
        setGoal(new ShoulderGoal(shoulderGoal));
    }

    public void setGoal(Climber.Goal climberGoal) {
        setGoal(new ClimberGoal(climberGoal));
    }

    @Override
    protected Status updateState() {
        return new Status();
    }

    public void resetShoulder() {
        shoulder.reset();
    }

    public void resetClimberPositions() {
        climber.resetLeftPosition();
        climber.resetRightPosition();
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        final boolean climberIsBelowArm =
                climber.getStatus().heightLeft() < Climber.MoveToPosition.BELOW_ARM.height()
                        && climber.getStatus().heightRight()
                                < Climber.MoveToPosition.BELOW_ARM.height();

        switch (goal) {
            case ShoulderGoal g -> {
                climber.setGoal(Climber.MoveToPosition.BOTTOM);

                if (climberIsBelowArm) {
                    shoulder.setGoal(g.shoulderGoal);
                } else {
                    shoulder.setGoal(new Shoulder.HoldPosition());
                }
            }
            case ClimberGoal g -> {
                final boolean climberGoalIsInnocuous =
                        switch (g.climberGoal()) {
                            case Climber.Stop c -> true;
                            case Climber.MotorPowers c -> false;
                            case Climber.MoveToPosition c -> (c.height()
                                    < Climber.MoveToPosition.BELOW_ARM.height());
                        };

                if (climberGoalIsInnocuous) {
                    climber.setGoal(g.climberGoal());
                } else {
                    shoulder.setGoal(Shoulder.RotateToPosition.TOP);
                    if (shoulder.getStatus().isNearTo(Shoulder.RotateToPosition.TOP)) {
                        climber.setGoal(g.climberGoal());
                    } else {
                        climber.setGoal(new Climber.Stop());
                    }
                }
            }
        }
    }
}
