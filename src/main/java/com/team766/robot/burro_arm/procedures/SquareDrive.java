package com.team766.robot.burro_arm.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class SquareDrive extends Procedure {

    public void run(final Context context) {
		for(int i=0;i<4;i++){
			new DriveStraight().run(context);
			new TurnRight().run(context);
		}
	
	}

}


