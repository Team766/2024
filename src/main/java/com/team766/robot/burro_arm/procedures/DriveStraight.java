package com.team766.robot.burro_arm.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.burro_arm.Robot;

public class DriveStraight extends Procedure {
	public void run(Context context) {
		Robot.drive.drive(0.5, 0);
		context.waitForSeconds(3);
		Robot.drive.stopDrive();
	}
}
