package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Elevator elevator;

	public static void robotInit() {
		// Initialize mechanisms here
		elevator = new Elevator();
	}
}
