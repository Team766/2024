package com.team766.robot.smores;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.smores.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static Lights lights;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        lights = new Lights();
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
