package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class GoForCubes extends Procedure {
	
	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.intake);
		
		Robot.intake.setGamePieceType(GamePieceType.CUBE);
	}
}
