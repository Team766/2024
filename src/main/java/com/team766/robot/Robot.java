package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static Drive drive;
	public static Gyro gyro;
	public static CANdleMech candle;


	public static void robotInit() {
		// Initialize mechanisms here
		drive = new Drive();
		gyro = new Gyro();
		candle = new CANdleMech();

	}
}
