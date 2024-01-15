package com.team766.robot.gatorade;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;

public class GatoradeConfigurator implements RobotConfigurator {
    public GatoradeConfigurator() {}

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
