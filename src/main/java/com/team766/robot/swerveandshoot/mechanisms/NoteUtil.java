package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;

public class NoteUtil extends Mechanism {

    public NoteUtil() {}

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

    public void goToNote() throws AprilTagGeneralCheckedException {
        if (getStatus() == 1) {

            double yawInDegrees = StaticCameras.noteDetectorCamera.getYawOfRing();

            double pitchInDegrees = StaticCameras.noteDetectorCamera.getPitchOfRing();

            if (Math.abs(yawInDegrees) < 0.5) {

                // Drive straight at the ring
                // Do a solid power, increment down?
                // Wait until ring is detected in intake

            } else {
                if (yawInDegrees < 0) {
                    // drive right
                } else {
                    // drive left
                }
            }
        }
    }
}
