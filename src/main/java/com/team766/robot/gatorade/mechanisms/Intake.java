package com.team766.robot.gatorade.mechanisms;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

/**
 * Basic intake.  Mounted on end of {@link Wrist}.  The intake can be controlled to attempt to
 * pull a game piece in via {@link #in}, release a contained game piece via {@link #out}, or stop
 * moving via {@link #stop}.
 *
 *
 * While the Intake does not maintain any state as to whether or not it contains a game piece,
 * it does have different modes of operation based on what kind of game piece it is prepared to
 * intake or outtake.  This is because the motor must spin in opposite directions to intake cubes
 * versus cones.
 */
public class Intake extends Mechanism<Intake.IntakeState, Intake.IntakeState> {

    private static final double POWER_IN = 0.3;
    private static final double POWER_OUT = 0.25;
    private static final double POWER_IDLE = 0.05;

    /**
     * The current type of game piece the Intake is preparing to hold or is holding.
     */
    public enum GamePieceType {
        CONE,
        CUBE
    }

    /**
     * The current movement state for the intake.
     */
    public enum MotorState {
        /**
         * Turns off the intake motor.
         */
        STOP,
        /**
         * Turns the intake to idle - run at low power to keep the game piece contained.
         */
        IDLE,
        /**
         * Turns the intake motor on in order to pull a game piece into the mechanism.
         */
        IN,
        /**
         * Turns the intake motor on in reverse direction, to release any contained game piece.
         */
        OUT
    }

    public record IntakeState(GamePieceType gamePieceType, MotorState state)
            implements Request, Status {
        @Override
        public boolean isDone() {
            return checkForStatusWith(IntakeState.class, this::equals);
        }
    }

    private final MotorController motor;

    /**
     * Constructs a new Intake.
     */
    public Intake() {
        motor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
    }

    @Override
    protected IntakeState getInitialRequest() {
        return new IntakeState(GamePieceType.CONE, MotorState.STOP);
    }

    @Override
    protected IntakeState run(IntakeState request, boolean isRequestNew) {
        if (isRequestNew) {
            switch (request.state) {
                case IN -> {
                    double power =
                            (request.gamePieceType == GamePieceType.CONE)
                                    ? POWER_IN
                                    : (-1 * POWER_IN);
                    motor.set(power);
                }
                case OUT -> {
                    double power =
                            (request.gamePieceType == GamePieceType.CONE)
                                    ? (-1 * POWER_OUT)
                                    : POWER_OUT;
                    motor.set(power);
                }
                case STOP -> {
                    motor.set(0.0);
                }
                case IDLE -> {
                    double power =
                            (request.gamePieceType == GamePieceType.CONE)
                                    ? POWER_IDLE
                                    : (-1 * POWER_IDLE);
                    motor.set(power);
                }
            }
        }
        return request;
    }
}
