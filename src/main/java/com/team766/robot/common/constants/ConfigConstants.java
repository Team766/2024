package com.team766.robot.common.constants;

/** Constants used for reading values from the config file. */
public final class ConfigConstants {
    // utility class
    private ConfigConstants() {}

    public static final String DRIVE_RIGHT = "drive.Right";
    public static final String DRIVE_LEFT = "drive.Left";

    // drive config values
    public static final String DRIVE_GYRO = "drive.Gyro";
    public static final String DRIVE_DRIVE_FRONT_RIGHT = "drive.DriveFrontRight";
    public static final String DRIVE_DRIVE_FRONT_LEFT = "drive.DriveFrontLeft";
    public static final String DRIVE_DRIVE_BACK_RIGHT = "drive.DriveBackRight";
    public static final String DRIVE_DRIVE_BACK_LEFT = "drive.DriveBackLeft";

    public static final String DRIVE_STEER_FRONT_RIGHT = "drive.SteerFrontRight";
    public static final String DRIVE_STEER_FRONT_LEFT = "drive.SteerFrontLeft";
    public static final String DRIVE_STEER_BACK_RIGHT = "drive.SteerBackRight";
    public static final String DRIVE_STEER_BACK_LEFT = "drive.SteerBackLeft";

    public static final String DRIVE_TARGET_ROTATION_PID = "drive.setpointPid";

    // pathplanner config values
    public static final String PATH_FOLLOWING_MAX_MODULE_SPEED_MPS =
            "followpath.maxSpeedMetersPerSecond";

    public static final String PATH_FOLLOWING_TRANSLATION_P = "followpath.translationP";
    public static final String PATH_FOLLOWING_TRANSLATION_I = "followpath.translationI";
    public static final String PATH_FOLLOWING_TRANSLATION_D = "followpath.translationD";
    public static final String PATH_FOLLOWING_ROTATION_P = "followpath.rotationP";
    public static final String PATH_FOLLOWING_ROTATION_I = "followpath.rotationI";
    public static final String PATH_FOLLOWING_ROTATION_D = "followpath.rotationD";
}
