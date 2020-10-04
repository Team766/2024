package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.RobotProvider;
import com.team766.hal.SolenoidController;

public class Launcher extends Mechanism {
	private SolenoidController pusher;

	public Launcher() {
		pusher = RobotProvider.instance.getSolenoid("launch");
	}

	public void setPusher(boolean extended) {
		checkContextOwnership();

		pusher.set(extended);
	}
}