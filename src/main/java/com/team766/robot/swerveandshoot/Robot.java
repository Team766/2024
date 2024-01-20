package com.team766.robot.swerveandshoot;

import com.team766.robot.swerveandshoot.mechanisms.*;

public class Robot {
    // Declare mechanisms here
    public static TempPickerUpper tempPickerUpper;
    public static TempShooter tempShooter;
    public static NoteUtil noteUtil;
	public static Lights lights;

    public static void robotInit() {
        tempPickerUpper = new TempPickerUpper();
        tempShooter = new TempShooter();
        noteUtil = new NoteUtil();
		lights = new Lights();
    }
}
