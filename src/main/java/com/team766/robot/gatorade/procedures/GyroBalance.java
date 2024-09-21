package com.team766.robot.gatorade.procedures;

import static com.team766.framework3.StatusBus.getStatusOrThrow;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.gatorade.constants.ChargeConstants;
import com.team766.robot.gatorade.mechanisms.Superstructure;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * {@link Procedure} to use the gyro to automatically balance on the charge station.
 * See GyroBalance.md for more details
 */
public class GyroBalance extends Procedure {

    // Direction determines which direction the robot moves
    private enum Direction {
        LEFT,
        RIGHT,
        STOP,
    }

    private final SwerveDrive drive;
    private final Superstructure superstructure;

    // absSpeed is unsigned speed value
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
    public GyroBalance(Alliance alliance, SwerveDrive drive, Superstructure superstructure) {
        this.alliance = alliance;
        this.drive = reserve(drive);
        this.superstructure = reserve(superstructure);
    }

    private double getAbsoluteTilt() {
        final double pitch = getStatusOrThrow(SwerveDrive.DriveStatus.class).pitch();
        final double roll = getStatusOrThrow(SwerveDrive.DriveStatus.class).roll();
        return Math.toDegrees(
                Math.acos(Math.cos(Math.toRadians(roll) * Math.cos(Math.toRadians(pitch)))));
    }

    public void run(Context context) {
        // extend wristvator to put CG in a place where robot can climb ramp
        superstructure.setRequest(Superstructure.MoveToPosition.EXTENDED_TO_MID);
        context.waitFor(() -> Superstructure.MoveToPosition.EXTENDED_TO_MID.isDone());

        // initialY is robot y position when balancing starts
        final double initialY =
                getStatusOrThrow(SwerveDrive.DriveStatus.class).currentPosition().getY();
        // Sets movement direction towards desired charge station.
        switch (alliance) {
            case Red:
                // If to the right of the charge station, go left
                if (initialY > ChargeConstants.RED_BALANCE_TARGET_X) {
                    direction = Direction.LEFT;
                    // If to the left of the charge station, go right
                } else {
                    direction = Direction.RIGHT;
                }
                break;
            case Blue:
                // Same logic for blue alliance coordinates
                if (initialY > ChargeConstants.BLUE_BALANCE_TARGET_X) {
                    direction = Direction.LEFT;
                } else {
                    direction = Direction.RIGHT;
                }
                break;
            default:
                log("Invalid alliance");
                return;
        }
        // log("direction: " + direction);

        // State: GROUND
        setDriveSpeed(SPEED_GROUND);
        context.waitFor(() -> getAbsoluteTilt() > LEVEL);

        // State: RAMP_TRANSITION
        setDriveSpeed(SPEED_TRANSITION);
        log("Transition, curState: RAMP_TRANSITION");
        context.waitFor(
                () -> {
                    final double tilt = getAbsoluteTilt();
                    return tilt < TOP_TILT && tilt > FLAP_TILT;
                });

        // State: RAMP_TILT
        setDriveSpeed(SPEED_TILT);
        log("Tilt, curState: RAMP_TILT");
        superstructure.setRequest(Superstructure.MoveToPosition.RETRACTED);

        double overshootSpeed = -SPEED_OVERSHOOT;
        while (true) {
            context.waitFor(() -> getAbsoluteTilt() < LEVEL);

            drive.setRequest(new SwerveDrive.SetCross());
            context.waitForSeconds(1);
            if (getAbsoluteTilt() < LEVEL) {
                // State: RAMP_LEVEL
                log("Overshoot, curState: RAMP_LEVEL");
                break;
            } else {
                // State: OVERSHOOT
                // Sets speed to negative to correct for overshooting
                overshootSpeed *= -OVERSHOOT_INCORRECT_MULT;
                setDriveSpeed(overshootSpeed);
                log("Overshoot, curState: OVERSHOOT");
            }
        }
    }

    private void setDriveSpeed(final double absSpeed) {
        // Both being on Red alliance and needing to move right would make the movement
        // direction negative, so this expression corrects for that
        double driveSpeed;
        if ((alliance == Alliance.Red) ^ (direction == Direction.RIGHT)) {
            driveSpeed = -absSpeed;
        } else {
            driveSpeed = absSpeed;
        }

        // Drives the robot with the calculated speed and direction
        drive.setRequest(new SwerveDrive.FieldOrientedVelocity(0, driveSpeed, 0));
    }
}
