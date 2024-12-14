package com.team766.robot.burro_arm;

import com.team766.framework.AutonomousMode;
import com.team766.framework.RuleEngine;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.burro_arm.mechanisms.*;
import com.team766.robot.common.mechanisms.BurroDrive;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    private BurroDrive drive;
    private Arm arm;
    private Gripper gripper;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        drive = new BurroDrive();
        arm = new Arm();
        gripper = new Gripper();
    }

    @Override
    public RuleEngine createOI() {
        return new OI(drive, arm, gripper);
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
