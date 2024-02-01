package com.team766.robot.akbear;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
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
