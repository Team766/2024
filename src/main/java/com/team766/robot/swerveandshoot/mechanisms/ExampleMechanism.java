package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;

public class ExampleMechanism extends Mechanism {
	private MotorController leftMotor;
	private MotorController rightMotor;

	public ExampleMechanism() {
		leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
		rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
	}

	public void setMotorPower(final double leftPower, final double rightPower) {
		checkContextOwnership();

		leftMotor.set(leftPower);
		rightMotor.set(rightPower);
	}
}