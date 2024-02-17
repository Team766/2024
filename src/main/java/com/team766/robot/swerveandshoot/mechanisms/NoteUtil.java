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
        yawPID = new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);
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

            if (power > 0 && yawInDegrees > 0) {
                power *= -1;
            }

            log("power: " + power);

            log("power: " + power);
            if (Math.abs(power) > 0.045) {
                // x needs inverted if camera is on front (found out through tests)
                Robot.drive.controlRobotOriented(power, 0, 0);
            } else {
                // Run intake the whole time
                Robot.tempPickerUpper.runIntake();
                Robot.drive.controlRobotOriented(0, -0.3, 0);
            }

            // double pitchInDegrees = StaticCameras.noteDetectorCamera.getPitchOfRing();

        }
    }

    public void test() {
        try {
            log(StaticCameras.noteDetectorCamera.getTransform3dOfRing().toString());
        } catch (AprilTagGeneralCheckedException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }

    // to be implemented once we have sensors
    public boolean hasNoteInIntake() {
        return false;
    }
}
