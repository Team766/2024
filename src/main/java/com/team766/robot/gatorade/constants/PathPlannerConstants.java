package com.team766.robot.gatorade.constants;

import com.pathplanner.lib.util.ReplanningConfig;
import edu.wpi.first.math.controller.HolonomicDriveController;

public final class PathPlannerConstants {
	// default replanning config values
	public static final ReplanningConfig REPLANNING_CONFIG = new ReplanningConfig();

	public static final HolonomicDriveController DRIVE_CONTROLLER = new HolonomicDriveController(null, null, null);
}
