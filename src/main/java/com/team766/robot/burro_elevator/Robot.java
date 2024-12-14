package com.team766.robot.burro_elevator;

import com.team766.framework.AutonomousMode;
import com.team766.framework.RuleEngine;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.burro_arm.mechanisms.Gripper;
import com.team766.robot.burro_elevator.mechanisms.*;
import com.team766.robot.common.mechanisms.BurroDrive;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    private BurroDrive drive;
    private Elevator elevator;
    private Gripper gripper;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        drive = new BurroDrive();
        elevator = new Elevator();
        gripper = new Gripper();
    }

    @Override
    public RuleEngine createOI() {
        return new OI(drive, elevator, gripper);
    }

    @Override
    public RuleEngine createLights() {
        return null;
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
