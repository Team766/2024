package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class ShootVelocityAndIntake extends Procedure {

	double speed;

	public ShootVelocityAndIntake(){
		this(2000);
	}

	public ShootVelocityAndIntake(double speed){
		this.speed = speed;
	}
	

	public void run(Context context) {
		context.takeOwnership(Robot.shooter);
		context.takeOwnership(Robot.intake);

		Robot.shooter.shoot(speed);
		context.waitFor(Robot.shooter::isCloseToExpectedSpeed);
		log("SUIIII");

		new IntakeIn().run(context);
		context.waitForSeconds(1.5);

		new IntakeStop().run(context);
		Robot.shooter.shoot(0);
	}
	
	
}
