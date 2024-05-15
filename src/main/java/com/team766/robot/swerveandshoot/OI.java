package com.team766.robot.swerveandshoot;

import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.mechanisms.ForwardApriltagCamera;
import com.team766.robot.swerveandshoot.mechanisms.NoteCamera;
import com.team766.robot.swerveandshoot.mechanisms.TempPickerUpper;
import com.team766.robot.swerveandshoot.mechanisms.TempShooter;
import com.team766.robot.swerveandshoot.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private final Drive drive;
    private final TempPickerUpper tempPickerUpper;
    private final TempShooter tempShooter;
    private final ForwardApriltagCamera forwardApriltagCamera;
    private final NoteCamera noteDetectorCamera;

    private final JoystickReader joystick0;
    private final JoystickReader joystick1;
    private final JoystickReader joystick2;

    public OI(
            Drive drive,
            TempPickerUpper tempPickerUpper,
            TempShooter tempShooter,
            ForwardApriltagCamera forwardApriltagCamera,
            NoteCamera noteDetectorCamera) {
        super(reservations(drive, tempPickerUpper, tempShooter));

        loggerCategory = Category.OPERATOR_INTERFACE;

        this.drive = drive;
        this.tempPickerUpper = tempPickerUpper;
        this.tempShooter = tempShooter;
        this.forwardApriltagCamera = forwardApriltagCamera;
        this.noteDetectorCamera = noteDetectorCamera;

        joystick0 = RobotProvider.instance.getJoystick(null, 0);
        joystick1 = RobotProvider.instance.getJoystick(null, 1);
        joystick2 = RobotProvider.instance.getJoystick(null, 2);
    }

    public void run(final Context context) {
        LaunchedContext visionProcedure = null;
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());

            RobotProvider.instance.refreshDriverStationData();

            // General drive util

            if (joystick0.getButtonPressed(2)) {
                drive.resetGyro();
            }
            if (Math.abs(joystick0.getAxis(0))
                            + Math.abs(joystick0.getAxis(1))
                            + Math.abs(joystick1.getAxis(0))
                    > 0.05) {
                drive.controlRobotOriented(
                        joystick0.getAxis(0) * .2,
                        -joystick0.getAxis(1) * .2,
                        joystick1.getAxis(0) * .2);
            } else {
                drive.stopDrive();
            }

            /*
             * The joystick operator will need to hold all of these buttons when using vision to score or pickup a note
             * The areas that this needs to happen are labled {SCORE1R, SCORE1L, PICKUP}
             */

            /*
             * SCORE1R
             * This is used to drive into the area labled 1R in the maker space.
             */
            if (joystick0.getButtonPressed(1)) {
                // Robot.speakerShooter.goToAndScore(SpeakerShooterPowerCalculator.makerSpace1R);
                visionProcedure = context.startAsync(new DriveToAndScoreAt(
                        ScoringPositions.makerSpace1R, drive, tempShooter, forwardApriltagCamera));
            }

            if (joystick0.getButtonReleased(1)) {
                visionProcedure.cancel();
            }

            /*
             * SCORE1L
             * This is used to drive into the area labled 1L in the maker space.
             */
            if (joystick0.getButtonPressed(2)) {
                // Robot.speakerShooter.goToAndScore(SpeakerShooterPowerCalculator.makerSpace1R);
                visionProcedure = context.startAsync(new DriveToAndScoreAt(
                        ScoringPositions.makerSpace1L, drive, tempShooter, forwardApriltagCamera));
            }

            if (joystick0.getButtonReleased(2)) {
                visionProcedure.cancel();
            }

            /*
             * PICKUP
             * This is used to go to the ring and "pick" it up, or in reality right now just nudge it and pretend like it was picked up
             */

            if (joystick1.getButtonPressed(1)) {
                visionProcedure = context.startAsync(
                        new PickupNote(drive, tempPickerUpper, noteDetectorCamera));
            }

            if (joystick1.getButtonReleased(1)) {
                visionProcedure.cancel();
            }
            // if (joystick1.getButton(1)) {
            //     try {
            //         switch (Robot.noteUtil.goToAndPickupNote()) {
            //             case NO_RING_IN_VIEW:
            //                 Robot.lights.signalNoRing();
            //                 break;
            //             case RING_IN_INTAKE:
            //                 Robot.lights.signalNoteInIntake();
            //                 break;
            //             case RING_IN_VIEW:
            //                 Robot.lights.signalRing();
            //                 break;
            //             default:
            //                 Robot.lights.turnLEDsOff();
            //                 break;
            //         }

            //     } catch (AprilTagGeneralCheckedException e) {
            //         Robot.lights.signalNoRing();
            //     }
            // }

            /*
             * This is used to display the status of the ring viewer if the operator does not want to go to the ring
             */
            // if (joystick1.getButton(2)) {
            //     switch (Robot.noteUtil.getStatus()) {
            //         case NO_RING_IN_VIEW:
            //             Robot.lights.signalNoRing();
            //             break;
            //         case RING_IN_INTAKE:
            //             Robot.lights.signalNoteInIntake();
            //             break;
            //         case RING_IN_VIEW:
            //             Robot.lights.signalRing();
            //             break;
            //         default:
            //             Robot.lights.turnLEDsOff();
            //             break;
            //     }
            // }
        }
    }
}
