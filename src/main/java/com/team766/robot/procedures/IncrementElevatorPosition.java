package com.team766.robot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.Robot;

public class IncrementElevatorPosition extends Procedure {

	@Override
	public void run(Context context) {
		context.takeOwnership(Robot.elevator);

		Robot.elevator.setSetpoint(Robot.elevator.getSetpoint() + 6);
	}

}