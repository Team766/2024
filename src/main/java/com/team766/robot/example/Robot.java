package com.team766.robot.example;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.example.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here

    public static void robotInit() {
        // Initialize mechanisms here
    }

    @Override
    public void initializeMechanisms() {
        robotInit();
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
