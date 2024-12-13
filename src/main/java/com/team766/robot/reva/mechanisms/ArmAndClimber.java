package com.team766.robot.reva.mechanisms;

import static com.team766.framework3.StatusBus.getStatusOrThrow;

import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.framework3.Superstructure;

public class ArmAndClimber
        extends Superstructure<
                ArmAndClimber.SuperstructureRequest, ArmAndClimber.SuperstructureStatus> {
    public record SuperstructureStatus() implements Status {}

    public sealed interface SuperstructureRequest extends Request {}

    // NOTE: This request type is private because we don't want to expose the ability
    // to send arbitrary requests to the individual mechanisms.
    private record SimpleRequest(
            Shoulder.ShoulderRequest shoulderRequest, Climber.ClimberRequest climberRequest)
            implements SuperstructureRequest {
        @Override
        public boolean isDone() {
            return shoulderRequest().isDone() && climberRequest().isDone();
        }
    }

    public record ShoulderRequest(Shoulder.ShoulderRequest shoulderRequest)
            implements SuperstructureRequest {
        @Override
        public boolean isDone() {
            return shoulderRequest.isDone();
        }
    }

    public record ClimberRequest(Climber.ClimberRequest climberRequest)
            implements SuperstructureRequest {
        @Override
        public boolean isDone() {
            return climberRequest.isDone();
        }
    }

    public static SimpleRequest makeStop() {
        return new SimpleRequest(new Shoulder.Stop(), new Climber.Stop());
    }

    public static SimpleRequest makeHoldPosition() {
        return new SimpleRequest(Shoulder.makeHoldPosition(), new Climber.Stop());
    }

    private final Shoulder shoulder;
    private final Climber climber;

    public ArmAndClimber() {
        this(new Shoulder(), new Climber());
    }

    public ArmAndClimber(Shoulder shoulder, Climber climber) {
        super(shoulder, climber);
        this.shoulder = shoulder;
        this.climber = climber;
    }

    public void setRequest(Shoulder.ShoulderRequest shoulderRequest) {
        checkContextReservation();
        setRequest(new ShoulderRequest(shoulderRequest));
    }

    public void setRequest(Climber.ClimberRequest climberRequest) {
        checkContextReservation();
        setRequest(new ClimberRequest(climberRequest));
    }

    public void resetShoulder() {
        checkContextReservation();
        shoulder.reset();
    }

    public void resetClimberPositions() {
        checkContextReservation();
        climber.resetLeftPosition();
        climber.resetRightPosition();
    }

    @Override
    protected SuperstructureRequest getInitialRequest() {
        return makeStop();
    }

    @Override
    protected SuperstructureStatus run(SuperstructureRequest request, boolean isRequestNew) {
        final boolean climberIsBelowArm =
                getStatusOrThrow(Climber.ClimberStatus.class).heightLeft()
                                < Climber.MoveToPosition.BELOW_ARM.height()
                        && getStatusOrThrow(Climber.ClimberStatus.class).heightRight()
                                < Climber.MoveToPosition.BELOW_ARM.height();

        switch (request) {
            case SimpleRequest g -> {
                climber.setRequest(g.climberRequest());
                shoulder.setRequest(g.shoulderRequest());
            }
            case ShoulderRequest g -> {
                climber.setRequest(Climber.MoveToPosition.BOTTOM);

                if (climberIsBelowArm) {
                    shoulder.setRequest(g.shoulderRequest());
                } else {
                    shoulder.setRequest(Shoulder.makeHoldPosition());
                }
            }
            case ClimberRequest g -> {
                final boolean climberRequestIsInnocuous =
                        switch (g.climberRequest()) {
                            case Climber.Stop c -> true;
                            case Climber.MotorPowers c -> false;
                            case Climber.MoveToPosition c -> (c.height()
                                    < Climber.MoveToPosition.BELOW_ARM.height());
                        };

                if (climberRequestIsInnocuous) {
                    climber.setRequest(g.climberRequest());
                } else {
                    shoulder.setRequest(Shoulder.RotateToPosition.TOP);
                    if (getStatusOrThrow(Shoulder.ShoulderStatus.class)
                            .isNearTo(Shoulder.RotateToPosition.TOP)) {
                        climber.setRequest(g.climberRequest());
                    } else {
                        climber.setRequest(new Climber.Stop());
                    }
                }
            }
        }

        return new SuperstructureStatus();
    }
}
