package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.robot.swerveandshoot.Robot;

public class NoteUtil extends Mechanism {

    // PID Controller that moves the robot horizontally [<------->] in order to line up with the
    // note
    private PIDController yawPID;

    public NoteUtil() {
        // Instantiating the PID controller with previous values that we have found work well.
        yawPID = new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);
        // Setting the setpoint at zero because the middle of the note should be the middle of the
        // camera.
        yawPID.setSetpoint(0.00);
    }

    public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

    public NoteUtil.status getStatus() {

        try {
            StaticCameras.noteDetectorCamera.getRing();
        } catch (AprilTagGeneralCheckedException e) {
            return status.NO_RING_IN_VIEW;
        }

        return status.RING_IN_VIEW;
    }

    public NoteUtil.status goToAndPickupNote() throws AprilTagGeneralCheckedException {

        try {
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
                return status.RING_IN_VIEW;

            } else {
                return status.RING_IN_INTAKE;
            }

        } catch (AprilTagGeneralCheckedException e) {
            return status.NO_RING_IN_VIEW;
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
