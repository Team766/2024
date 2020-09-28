package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static Intake intake;
	public static Launcher launcher;

	public static void robotInit() {
		// Initialize mechanisms here
		drive = new Drive();
		intake = new Intake();
		launcher = new Launcher();
	}
}
