package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;

public class MyAutonomous extends Procedure {

	public void run(Context context) {
		LaunchedContext driveProcedure = context.startAsync(new DriveStraight());
		LaunchedContext armProcedure = context.startAsync(new RaiseArm());
		context.waitFor(driveProcedure, armProcedure);
	}

}