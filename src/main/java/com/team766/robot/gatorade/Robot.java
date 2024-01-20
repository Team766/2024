package com.team766.robot.gatorade;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.mechanisms.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static Intake intake;
    public static Wrist wrist;
    public static Elevator elevator;
    public static Shoulder shoulder;
    public static Drive drive;
    public static Lights lights;

    @Override
    public void initializeMechanisms() {
        intake = new Intake();
        wrist = new Wrist();
        elevator = new Elevator();
        shoulder = new Shoulder();
        drive = new Drive();
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
