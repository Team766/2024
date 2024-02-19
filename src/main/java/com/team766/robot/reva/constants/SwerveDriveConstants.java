package com.team766.robot.reva.constants;

public final class SwerveDriveConstants {

    // defines where the wheels are in relation to the center of the robot
    // allows swerve drive code to calculate how to turn
    public static final double FL_X = -1;
    public static final double FL_Y = 1;
    public static final double FR_X = 1;
    public static final double FR_Y = 1;
    public static final double BL_X = -1;
    public static final double BL_Y = -1;
    public static final double BR_X = 1;
    public static final double BR_Y = -1;

    public static final String SWERVE_CANBUS = "swerve";

    public static final double DRIVE_MOTOR_CURRENT_LIMIT = 35;
    public static final double STEER_MOTOR_CURRENT_LIMIT = 30;

    // Circumference of the wheels. It was measured to be 30.5cm, then experimentally this value had
    // an error of 2.888%. This was then converted to meters.
    public static final double WHEEL_CIRCUMFERENCE = 30.5 * 1.02888 / 100;

    // The distance between the center of a wheel to the center of an adjacent wheel, assuming the
    // robot is square. This was measured as 20.5 inches, then converted to meters.
    public static final double DISTANCE_BETWEEN_WHEELS = 24.75 * 2.54 / 100;
}
