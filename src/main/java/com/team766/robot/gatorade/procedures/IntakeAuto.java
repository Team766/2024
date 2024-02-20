package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class IntakeAuto extends Procedure {
	public void run(Context context) {
		context.takeOwnership(Robot.drive);
		Robot.drive.resetGyro();
		Robot.drive.setCurrentPosition(new Pose2d(2.00, 7.00, new Rotation2d(0)));
		new IntakeIn().run(context);
		new FollowPath("Intake_Path_1").run(context);
		new IntakeIdle().run(context);
		new FollowPath("Intake_Path_2").run(context);
		new IntakeOut().run(context);
		new SetCross().run(context);
		context.waitForSeconds(1);
		new IntakeStop().run(context);
		new FollowPath("Intake_Path_3").run(context);
		new IntakeIn().run(context);
		new FollowPath("Intake_Path_4").run(context);
		new SetCross().run(context);
		new IntakeOut().run(context);
		context.waitForSeconds(2);
		new IntakeStop().run(context);
	}
	
}
