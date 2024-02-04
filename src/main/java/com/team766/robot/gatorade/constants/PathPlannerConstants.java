package com.team766.robot.gatorade.constants;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.config.ConfigFileReader;
import com.team766.controllers.PIDController;
import com.team766.hal.RobotProvider;

public final class PathPlannerConstants {
	// default replanning config values
	public static final ReplanningConfig REPLANNING_CONFIG = new ReplanningConfig();

	// PID constants for drive controller
	public static final double TRANSLATION_P = 0;
	public static final double TRANSLATION_I = 0;
	public static final double TRANSLATION_D = 0;

	public static final double ROTATION_P = 0;
	public static final double ROTATION_I = 0;
	public static final double ROTATION_D = 0;

	// default values
	public static final double MAX_SPEED_MPS = 1;
}
