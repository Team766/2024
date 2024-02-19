package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TestPathAuto extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		Robot.drive.resetGyro();
		Robot.drive.setCurrentPosition(new Pose2d(2.00, 7.00, new Rotation2d(0)));
		log("set current pos: " + Robot.drive.getCurrentPosition());
		new FollowPath("RotationTest").run(context);
		new SetCross().run(context);
	}
	
}
