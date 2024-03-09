package com.team766.robot.proximity_burrobot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.proximity_burrobot.mechanisms.*;
import com.team766.robot.reva.mechanisms.RightProximitySensor;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static RightProximitySensor proximitySensor;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        proximitySensor = new RightProximitySensor();
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
