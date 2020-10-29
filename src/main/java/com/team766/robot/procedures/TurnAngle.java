package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.Robot;

public class TurnAngle extends Procedure {

	public void run(Context context) {
		context.takeOwnership(Robot.drive);

		Robot.drive.resetGyro();

		Robot.drive.setArcadeDrivePower(0.0, 0.25);
		context.waitFor(() -> Robot.drive.getGyroAngle() <= -45);
		Robot.drive.setArcadeDrivePower(0.0, 0.10);
		context.waitFor(() -> Robot.drive.getGyroAngle() <= -80);
		Robot.drive.setArcadeDrivePower(0.0, 0.01);
		context.waitFor(() -> Robot.drive.getGyroAngle() <= -90);

		Robot.drive.setDrivePower(0.0, 0.0);
	}

}
