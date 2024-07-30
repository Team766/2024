package com.team766.robot.burro_arm;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.burro_arm.mechanisms.*;
import com.team766.robot.common.mechanisms.BurroDrive;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static BurroDrive drive;
    public static Arm arm;
    public static Gripper gripper;

    @Override
    public void initializeMechanisms() {
        // Initialize mechanisms here
        drive = new BurroDrive();
        arm = new Arm();
        gripper = new Gripper();
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
