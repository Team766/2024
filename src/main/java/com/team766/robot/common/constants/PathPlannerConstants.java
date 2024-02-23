package com.team766.robot.common.constants;

import com.pathplanner.lib.util.ReplanningConfig;

public final class PathPlannerConstants {
    // default replanning config values
    public static final ReplanningConfig REPLANNING_CONFIG = new ReplanningConfig(false, false);

    // PID constants for drive controller
    // TODO: change pathplanner constants
    public static final double TRANSLATION_P = 0.1;
    public static final double TRANSLATION_I = 0;
    public static final double TRANSLATION_D = 0.05;

    public static final double ROTATION_P = 4.00;
    public static final double ROTATION_I = 0;
    public static final double ROTATION_D = 0;

    // default values
    public static final double MAX_SPEED_MPS = 4.5;
}
