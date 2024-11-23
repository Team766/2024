package com.team766.robot.rookie_bot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.rookie_bot.mechanisms.Elevator;
import com.team766.robot.rookie_bot.mechanisms.Intake;
import com.team766.robot.rookie_bot.mechanisms.Drive;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static Drive drive;  
    public static Elevator elevator;
    public static Intake intake;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        drive = new Drive();
        elevator = new Elevator();
        intake = new Intake ();

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
