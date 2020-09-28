package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;
import com.team766.hal.MotorController;

public class Intake extends Mechanism {
	private SolenoidController m_intakeArm;
	private MotorController m_intakeWheels;

	public Intake() {
		m_intakeArm = RobotProvider.instance.getSolenoid("intakeArm");
		m_intakeWheels = RobotProvider.instance.getMotor("intake");
	}

	public void setIntakePower(double intakePower) {
		checkContextOwnership();
		m_intakeWheels.set(intakePower);
	}

	public void setIntakeArm(boolean state) {
		checkContextOwnership();
		m_intakeArm.set(state);
	}
}