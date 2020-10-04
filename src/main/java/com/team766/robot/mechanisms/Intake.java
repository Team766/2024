package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.MotorController;

public class Intake extends Mechanism {
	private SolenoidController m_extend;
	private MotorController m_wheels;

	public Intake() {
		m_extend = RobotProvider.instance.getSolenoid("intakeArm");
		m_wheels = RobotProvider.instance.getMotor("intakeWheels");
	}

	public void setExtended(boolean extended) {
		checkContextOwnership();

		m_extend.set(extended);
	}

	public void setWheelPower(double power) {
		checkContextOwnership();

		m_wheels.set(power);
	}
}