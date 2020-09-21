package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;

public class DriveSquare extends Procedure {

	public void run(Context context) {
		// This loop repeats 4 times.
		for (int i = 0; i < 4; ++i) {
			// Drive along the side of the square
			new DriveStraight().run(context);

			// Turn at the corner
			new TurnRight().run(context);
		}
	}

}