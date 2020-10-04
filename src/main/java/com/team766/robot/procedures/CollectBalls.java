package com.team766.robot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;

public class CollectBalls extends Procedure {

	public void run(Context context) {
		new StartIntake().run(context);

		new DriveStraight().run(context);

		new StopIntake().run(context);
	}

}