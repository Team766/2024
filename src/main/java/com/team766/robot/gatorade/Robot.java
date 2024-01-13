package com.team766.robot.gatorade;

import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.mechanisms.*;

public class Robot {
    // Declare mechanisms here
    public static Intake intake;
    public static Wrist wrist;
    public static Elevator elevator;
    public static Shoulder shoulder;
    public static Drive drive;
    public static Lights lights;

    public static void robotInit() {
        // Initialize mechanisms here
        intake = new Intake();
        wrist = new Wrist();
        elevator = new Elevator();
        shoulder = new Shoulder();
        drive = new Drive();
        lights = new Lights();
    }
}
