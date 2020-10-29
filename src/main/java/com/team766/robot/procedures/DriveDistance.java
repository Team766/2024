package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.Robot;

public class DriveDistance extends Procedure {

	public void run(Context context) {
		context.takeOwnership(Robot.drive);

		Robot.drive.resetEncoders();

		Robot.drive.setDrivePower(0.25, 0.25);

		context.waitFor(() -> Robot.drive.getEncoderDistance() >= 60.0);

		Robot.drive.setDrivePower(0.0, 0.0);
	}

}