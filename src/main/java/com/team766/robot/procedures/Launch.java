package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class Launch extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.launcher);

		Robot.launcher.setPusher(true);

		context.waitForSeconds(0.25);

		Robot.launcher.setPusher(false);

		context.waitForSeconds(0.25);
	}
}