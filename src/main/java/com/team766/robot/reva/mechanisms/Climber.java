package com.team766.robot.reva.mechanisms;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;

public class Climber extends Mechanism {
	
	private MotorController climberMotor;
	private MotorController rightMotor;

	public Climber() {
		climberMotor = ...;
		rightMotor = ...;
		rightMotor.follow(climberMotor);
	}

	private double heightToRotations(double height) {
		return height * GEAR_RATIO * CONVERSION;
	}

	private double rotationsToHeight(double rotations) {
		// ...
	}

	public void setClimbPosition(double TargetHeight){
		double r = heightToRotations(TargetHeight);
		climberMotor.set(MotorController.ControlMode.Position, r);
	}

	public double getClimberPosition(){
		return rotationsToHeight(climberMotor.getSensorPosition());
	}
	public void nudgeUp()
	{

		// one nudge is ##### cm
	}

	public void nudgeDown()
	{
		// one nudge is ##### cm
	}

	@Override
	public void run() {
		SmartDashboard.putNumber("[CLIMBER] Rotations", climberMotor.getSensorPosition());
		
		SmartDashboard.putNumber("[CLIMBER] Position", getClimberPosition);
	}
}
