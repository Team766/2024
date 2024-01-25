package com.team766.robot.swerveandshoot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms here
    public static TempPickerUpper tempPickerUpper;
    public static TempShooter tempShooter;
    public static NoteUtil noteUtil;
    public static Lights lights;
    public static Drive drive;

    @Override
    public void initializeMechanisms() {
        tempPickerUpper = new TempPickerUpper();
        tempShooter = new TempShooter();
        noteUtil = new NoteUtil();
        lights = new Lights();
        drive = new Drive();
    }

    @Override
    public Procedure createOI() {
        return new OI();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
