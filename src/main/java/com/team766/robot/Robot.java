package com.team766.robot;

import com.team766.robot.mechanisms.*;

public class Robot {
	// Declare mechanisms here
	public static TempPickerUpper tempPickerUpper;
	public static TempShooter tempShooter;


	public static void robotInit() {
		tempPickerUpper = new TempPickerUpper();
		tempShooter = new TempShooter();
	}
}
