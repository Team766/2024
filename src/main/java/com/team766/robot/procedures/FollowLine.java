package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.Robot;

public class FollowLine extends Procedure {

	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		context.takeOwnership(Robot.lineSensors);

		while (true) {
			// Add line following code here
			double steering = 0;
			if (Robot.lineSensors.getLineSensorLeft()) {
				steering = -0.45;
			} else if (Robot.lineSensors.getLineSensorRight()) {
				steering = 0.45;
			}
			if (Robot.lineSensors.getLineSensorCenter()) {
				steering /= 4;
			}
			Robot.drive.setArcadeDrivePower(0.20, steering);

			context.yield();
		}
	}

}
