package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.controllers.PIDController;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;
import com.team766.robot.swerveandshoot.Robot;


public class NoteUtil extends Mechanism {

    private PIDController yawPID;

    public NoteUtil() {
        // set reasonable deadzone!
        yawPID = new PIDController(0.01, 0, 0, 0, -0.15, 0.15, 0.2);
        yawPID.setSetpoint(0.00);
    }

    public String toString() {
        try {
            return "Yaw: "
                    + StaticCameras.noteDetectorCamera.getYawOfRing()
                    + " Pitch: "
                    + StaticCameras.noteDetectorCamera.getPitchOfRing();
        } catch (AprilTagGeneralCheckedException e) {
            return "ERROR: " + e.toString();
        }
    }

    public int getStatus() {

        try {
            StaticCameras.noteDetectorCamera.getRing();
        } catch (AprilTagGeneralCheckedException e) {
            return 2;
        }

        return 1;
    }

    public void goToAndPickupNote() throws AprilTagGeneralCheckedException {

        if (!hasNoteInIntake()) {

            double yawInDegrees = StaticCameras.noteDetectorCamera.getYawOfRing();
            yawPID.calculate(yawInDegrees);
            double power = yawPID.getOutput();

            log("power: " + power);
            if (Math.abs(power) > 0.035) {
                // x needs inverted (found out through tests)
                Robot.drive.controlRobotOriented(-power, 0, 0);
            } else {
                // Run intake the whole time
                Robot.tempPickerUpper.runIntake();
                Robot.drive.controlRobotOriented(0, 0.2, 0);
            }

            // double pitchInDegrees = StaticCameras.noteDetectorCamera.getPitchOfRing();

        }
    }

    // to be implemented once we have sensors
    public boolean hasNoteInIntake() {
        return false;
    }
}
