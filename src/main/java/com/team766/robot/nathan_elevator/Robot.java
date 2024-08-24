package com.team766.robot.nathan_elevator;

import com.team766.framework.AutonomousMode;
import com.team766.robot.common.mechanisms.BurroDrive;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.example.mechanisms.*;
import com.team766.robot.nathan_elevator.mechanisms.elevator;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static elevator testElevator;
    public static BurroDrive drive;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        testElevator = new elevator();
        drive = new BurroDrive();
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
