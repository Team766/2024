package com.team766.robot.perceptor;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.perceptor.mechanisms.Drive;

public class Robot implements RobotConfigurator {
    public static Drive drive;

    @Override
    public void initializeMechanisms() {
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
