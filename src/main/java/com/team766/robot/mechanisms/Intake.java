package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.MotorController;

public class Intake extends Mechanism {
	private SolenoidController extend;
	private MotorController wheels;

	public Intake() {
		extend = RobotProvider.instance.getSolenoid("intakeArm");
		wheels = RobotProvider.instance.getMotor("intakeWheels");
	}

	public void setExtended(boolean extended) {
		checkContextOwnership();

		extend.set(extended);
	}

	public void setWheelPower(double power) {
		checkContextOwnership();

		wheels.set(power);
	}
}