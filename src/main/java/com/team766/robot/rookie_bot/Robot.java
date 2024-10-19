package com.team766.robot.rookie_bot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.rookie_bot.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static Drive drive;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
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
