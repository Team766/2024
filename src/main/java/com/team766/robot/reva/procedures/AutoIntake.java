package com.team766.robot.reva.procedures;

import java.util.function.BooleanSupplier;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class AutoIntake extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.shoulder);
		Robot.shoulder.rotate(ShoulderPosition.BOTTOM);
		context.startAsync(new IntakeUntilIn());
	}
}
