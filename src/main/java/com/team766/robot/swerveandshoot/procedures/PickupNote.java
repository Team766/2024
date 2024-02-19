package com.team766.robot.swerveandshoot.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.robot.swerveandshoot.Robot;
import com.team766.robot.swerveandshoot.VisionPIDProcedure;

public class PickupNote extends VisionPIDProcedure {

    public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

    // button needs to be held down
    public void run(Context context) {
        yawPID.setSetpoint(0.00);
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.tempPickerUpper);

        try {
            while (!Robot.tempPickerUpper.hasNoteInIntake()) {

                double yawInDegrees = Robot.noteDetectorCamera.getCamera().getYawOfRing();
                yawPID.calculate(yawInDegrees);
                double power = yawPID.getOutput();

                if (power > 0 && yawInDegrees > 0) {
                    power *= -1;
                }

                log("power: " + power);

                if (Math.abs(power) > 0.045) {
                    // x needs inverted if camera is on front (found out through tests)
                    Robot.drive.controlRobotOriented(power, 0, 0);
                } else {
                    // Run intake the whole time
                    Robot.tempPickerUpper.runIntake();
                    Robot.drive.controlRobotOriented(0, -0.3, 0);
                }

                // double pitchInDegrees = Robot.noteDetectorCamera.getCamera().getPitchOfRing();
                // return status.RING_IN_VIEW;

            }
            Robot.tempPickerUpper.runIntake();

        } catch (AprilTagGeneralCheckedException e) {
            // return status.NO_RING_IN_VIEW;
        }
    }
}
