package com.team766.robot.swerveandshoot.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.swerveandshoot.Robot;
import com.team766.robot.swerveandshoot.VisionPIDControllers;

public class PickupNote extends Procedure{

	public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

	
	public void run(Context context) {
		VisionPIDControllers.yawPID.setSetpoint(0.00);
		try {
            if (!Robot.tempPickerUpper.hasNoteInIntake()) {

                double yawInDegrees = Robot.noteDetectorCamera.getCamera().getYawOfRing();
                VisionPIDControllers.yawPID.calculate(yawInDegrees);
                double power = VisionPIDControllers.yawPID.getOutput();

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
                //return status.RING_IN_VIEW;

            } else {
                //return status.RING_IN_INTAKE;
            }

        } catch (AprilTagGeneralCheckedException e) {
            //return status.NO_RING_IN_VIEW;
        }
	}
	
	
}
