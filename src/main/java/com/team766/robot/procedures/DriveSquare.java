package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;

public class DriveSquare extends Procedure {

	public void run(Context context) {
		// First side
		new DriveStraight().run(context);

		// First corner
		new TurnRight().run(context);

		// Second side
		new DriveStraight().run(context);

		// Second corner
		new TurnRight().run(context);

		// Third side
		new DriveStraight().run(context);

		// Third corner
		new TurnRight().run(context);

		// Fourth side
		new DriveStraight().run(context);

		// Fourth corner
		new TurnRight().run(context);
	}

}