package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.DigitalInputReader;
import com.team766.hal.RobotProvider;

public class LineSensors extends Mechanism {
	private DigitalInputReader lineSensorLeft;
	private DigitalInputReader lineSensorCenter;
	private DigitalInputReader lineSensorRight;

	public LineSensors() {
		lineSensorLeft = RobotProvider.instance.getDigitalInput("lineSensorLeft");
		lineSensorCenter = RobotProvider.instance.getDigitalInput("lineSensorCenter");
		lineSensorRight = RobotProvider.instance.getDigitalInput("lineSensorRight");
	}

	public boolean getLineSensorLeft() {
		return lineSensorLeft.get();
	}

	public boolean getLineSensorCenter() {
		return lineSensorCenter.get();
	}

	public boolean getLineSensorRight() {
		return lineSensorRight.get();
	}
}