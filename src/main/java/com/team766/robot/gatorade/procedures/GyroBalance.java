package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.constants.ChargeConstants;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * {@link Procedure} to use the gyro to automatically balance on the charge station.
 * See GyroBalance.md for more details
 */
public class GyroBalance extends Procedure {

    // State machine with 4 states for position on ramp
    private enum State {
        GROUND,
        RAMP_TRANSITION,
        RAMP_TILT,
        RAMP_LEVEL,
        OVERSHOOT
    }

    // Direction determines which direction the robot moves
    private enum Direction {
        LEFT,
        RIGHT,
        STOP,
    }

    // tilt is the overall combination of pitch and roll
    private double tilt = getAbsoluteTilt(Robot.drive.getPitch(), Robot.drive.getRoll());

    // absSpeed is unsigned speed value
    private double absSpeed;
    private State prevState;
    private State curState;
    private Direction direction;
    private final Alliance alliance;

    private final double TOP_TILT = 15.0;
    private final double FLAP_TILT = 11;

    // Tweak these values to adjust how the robot balances
    private final double LEVEL = 7;
    private final double CORRECTION_DELAY = 0.7;
    private final double SPEED_GROUND = .3;
    private final double SPEED_TRANSITION = .25;
    private final double SPEED_TILT = .12;
    private final double SPEED_OVERSHOOT = .08;
    private final double OVERSHOOT_INCORRECT_MULT = 0.5;

    /**
     * Constructor to create a new GyroBalance instance
     * @param alliance Alliance for setting direction towards charge station
     */
    public GyroBalance(Alliance alliance) {
        this.alliance = alliance;
    }

    private double getAbsoluteTilt(double pitch, double roll) {
        return Math.toDegrees(
                Math.acos(Math.cos(Math.toRadians(roll) * Math.cos(Math.toRadians(pitch)))));
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);

        // curY is current robot y position
        double curY = Robot.drive.getCurrentPosition().getY();

        // driveSpeed is actual value of speed passed into the swerveDrive method
        double driveSpeed = 1;

        // extend wristvator to put CG in a place where robot can climb ramp
        context.runSync(new ExtendWristvatorToMid());

        // Sets movement direction ground state if on ground
        setDir(curY);

        // sets starting state if not on ground
        if (tilt < LEVEL && curState != State.GROUND) {
            curState = State.RAMP_LEVEL;
        } else if (tilt < TOP_TILT && tilt > FLAP_TILT) {
            curState = State.RAMP_TILT;
        } else if (tilt > LEVEL) {
            curState = State.RAMP_TRANSITION;
        }

        do {
            // Sets prevState to the current state and calculates curState
            prevState = curState;
            curY = Robot.drive.getCurrentPosition().getY();
            tilt = getAbsoluteTilt(Robot.drive.getPitch(), Robot.drive.getRoll());
            // log("curX:" + curX);
            // log("direction: " + direction);
            setState(context);

            // Both being on Red alliance and needing to move right would make the movement
            // direction negative, so this expression corrects for that
            if ((alliance == Alliance.Red) ^ (direction == Direction.RIGHT)) {
                driveSpeed = -absSpeed;
            } else {
                driveSpeed = absSpeed;
            }

            // Drives the robot with the calculated speed and direction
            Robot.drive.controlFieldOriented(0, driveSpeed, 0);
            context.yield();
        }
        // Loops until robot is level or until a call to the abort() method
        while (!(curState == State.RAMP_LEVEL));
    }

    // Sets state in state machine, see more details in GyroBalance.md
    private void setState(Context context) {
        if (prevState == State.GROUND && tilt > LEVEL) {
            curState = State.RAMP_TRANSITION;
            absSpeed = SPEED_TRANSITION;
            log("Transition, prevState: " + prevState + ", curState: " + curState);
        } else if (prevState == State.RAMP_TRANSITION && tilt < TOP_TILT && tilt > FLAP_TILT) {
            curState = State.RAMP_TILT;
            absSpeed = SPEED_TILT;
            context.startAsync(new RetractWristvator());
            log("Tilt, prevState: " + prevState + ", curState: " + curState);
        } else if (prevState == State.RAMP_TILT && tilt < LEVEL) {
            curState = State.OVERSHOOT;
            // If level, sets speed to negative to correct for overshooting
            absSpeed = SPEED_OVERSHOOT;
            absSpeed = -absSpeed;
            log("Overshoot, prevState: " + prevState + ", curState: " + curState);
        } else if (prevState == State.OVERSHOOT && tilt < LEVEL) {
            context.startAsync(new SetCross());
            log("Level, prevState: " + prevState + ", curState: " + curState);
            context.waitForSeconds(1);
            tilt = getAbsoluteTilt(Robot.drive.getPitch(), Robot.drive.getRoll());
            if (tilt < LEVEL) {
                curState = State.RAMP_LEVEL;
            } else {
                absSpeed *= -OVERSHOOT_INCORRECT_MULT;
            }
        }
        if (curState == State.GROUND) {
            absSpeed = SPEED_GROUND;
        }
    }

    /**
     * Sets direction towards desired charge station
     * If robot is level and outside of charge station boundaries, sets state to ground
     * @param curY current robot x position
     */
    private void setDir(double curY) {
        switch (alliance) {
            case Red:
                // If to the right of the charge station, go left
                if (curY > ChargeConstants.RED_BALANCE_TARGET_X) {
                    // If level and outside of charge station boundaries, set state to ground
                    if (tilt < LEVEL && curY > ChargeConstants.RED_RIGHT_PT) {
                        curState = State.GROUND;
                    }
                    direction = Direction.LEFT;
                    // If to the left of the charge station, go right
                } else {
                    if (tilt < LEVEL && curY < ChargeConstants.RED_LEFT_PT) {
                        curState = State.GROUND;
                    }
                    direction = Direction.RIGHT;
                }
                break;
            case Blue:
                // Same logic for blue alliance coordinates
                if (curY > ChargeConstants.BLUE_BALANCE_TARGET_X) {
                    if (tilt < LEVEL && curY > ChargeConstants.BLUE_RIGHT_PT) {
                        curState = State.GROUND;
                    }
                    direction = Direction.LEFT;
                } else {
                    if (tilt < LEVEL && curY < ChargeConstants.BLUE_LEFT_PT) {
                        curState = State.GROUND;
                    }
                    direction = Direction.RIGHT;
                }
                break;
            default:
                log("Invalid alliance");
        }
    }
}
