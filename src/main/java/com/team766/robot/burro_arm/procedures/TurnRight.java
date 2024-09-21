package com.team766.robot.burro_arm.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.burro_arm.Robot;

public class TurnRight extends Procedure{
	public void run(Context context) {
		Robot.drive.setDrivePower(0.5, 0.0);
		context.waitForSeconds(1);
		Robot.drive.setDrivePower(0,0);

	}

}
