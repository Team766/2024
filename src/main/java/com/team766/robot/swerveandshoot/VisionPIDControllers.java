package com.team766.robot.swerveandshoot;

import com.team766.controllers.PIDController;

public class VisionPIDControllers {
	public static PIDController xPID = new PIDController(0.4, 0, 0, 0, -0.75, 0.75, 0.02);
    public static PIDController yPID = new PIDController(0.18, 0, 0, 0, -0.75, 0.75, 0.02);

	public static PIDController yawPID = new PIDController(0.02, 0.001, 0, 0, -0.25, 0.25, 3);
}
