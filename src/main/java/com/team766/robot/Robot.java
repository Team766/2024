package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static Launcher launcher;
	public static Intake intake;
	public static LineSensors lineSensors;

	public static void robotInit() {
		// Initialize mechanisms here
		drive = new Drive();
		launcher = new Launcher();
		intake = new Intake();
		lineSensors = new LineSensors();
	}
}
