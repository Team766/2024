package com.team766.robot.example;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;

public class ExampleConfigurator implements RobotConfigurator {
    public ExampleConfigurator() {}

    public void initializeMechanisms() {
        Robot.robotInit();
    }

    public Procedure createOI() {
        return new OI();
    }

    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
