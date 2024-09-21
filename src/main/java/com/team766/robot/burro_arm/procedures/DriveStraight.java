package com.team766.robot.burro_arm.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class DriveStraight extends Procedure {

    	public void run(final Context context) {
		Robot.Drive.setDriverPower(0.5, 0.5);

		context.waitForSeconds(2)
	
		Robot.drive.setDrivePower(0,0)

	}	
}