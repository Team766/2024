package com.team766.robot.swerveandshoot.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.VisionPIDProcedure;
import com.team766.robot.swerveandshoot.mechanisms.NoteCamera;
import com.team766.robot.swerveandshoot.mechanisms.TempPickerUpper;

public class PickupNote extends VisionPIDProcedure {

    public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

    private final Drive drive;
    private final TempPickerUpper tempPickerUpper;
    private final NoteCamera noteDetectorCamera;

    public PickupNote(Drive drive, TempPickerUpper tempPickerUpper, NoteCamera noteDetectorCamera) {
        super(reservations(drive, tempPickerUpper));
        this.drive = drive;
        this.tempPickerUpper = tempPickerUpper;
        this.noteDetectorCamera = noteDetectorCamera;
    }

    // button needs to be held down
    public void run(Context context) {
        yawPID.setSetpoint(0.00);

        try {
            while (!tempPickerUpper.hasNoteInIntake()) {

                double yawInDegrees = noteDetectorCamera.getCamera().getYawOfRing();
                yawPID.calculate(yawInDegrees);
                double power = yawPID.getOutput();

                if (power > 0 && yawInDegrees > 0) {
                    power *= -1;
                }

                log("power: " + power);

                if (Math.abs(power) > 0.045) {
                    // x needs inverted if camera is on front (found out through tests)
                    drive.setGoal(new Drive.RobotOrientedVelocity(power, 0, 0));
                } else {
                    // Run intake the whole time
                    tempPickerUpper.runIntake();
                    drive.setGoal(new Drive.RobotOrientedVelocity(0, -0.3, 0));
                }

                // double pitchInDegrees = Robot.noteDetectorCamera.getCamera().getPitchOfRing();
                // return status.RING_IN_VIEW;

                context.yield();
            }
            // Robot.tempPickerUpper.runIntake();
            // Todo: create method to stop intake
        } catch (AprilTagGeneralCheckedException e) {
            // return status.NO_RING_IN_VIEW;
        }
    }
}
