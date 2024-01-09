package com.team766.hal;

public final class RobotSelector {
	private RobotSelector() {}

	public static RobotStuff createStuff(String robotStuff) throws Exception {
		Class<RobotStuff> clazz = (Class<RobotStuff>) Class.forName(robotStuff);
		return clazz.getDeclaredConstructor().newInstance();
	}
}
