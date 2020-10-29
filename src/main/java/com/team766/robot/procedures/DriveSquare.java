package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.logging.Category;
import com.team766.framework.Context;

public class DriveSquare extends Procedure {

	public DriveSquare() {
		loggerCategory = Category.AUTONOMOUS;
	}

	public void run(Context context) {
		// First side
		new DriveDistance().run(context);
		log("First side complete");

		// First corner
		new TurnAngle().run(context);
		log("First corner complete");

		// Second side
		new DriveDistance().run(context);
		log("Second side complete");

		// Second corner
		new TurnAngle().run(context);
		log("Second corner complete");

		// Third side
		new DriveDistance().run(context);
		log("Third side complete");

		// Third corner
		new TurnAngle().run(context);
		log("Third corner complete");

		// Fourth side
		new DriveDistance().run(context);
		log("Fourth side complete");

		// Fourth corner
		new TurnAngle().run(context);
		log("Fourth corner complete");
	}

}