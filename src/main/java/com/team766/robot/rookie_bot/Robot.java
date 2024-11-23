package com.team766.robot.rookie_bot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.mechanisms.BurroDrive;
import com.team766.robot.rookie_bot.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static BurroDrive drive;
    public static Elevator elevator;
    public static Intake Intake;
    //public static Launcher launcher;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        drive = new BurroDrive();
        elevator= new Elevator();
        Intake = new Intake();
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
