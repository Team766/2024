package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;

public class IntakeIn extends Procedure{
	public void run(Context context){
		context.takeOwnership(Robot.intake);
		Robot.intake.in();
	}
}
