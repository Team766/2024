package com.team766.robot.common.constants;

/** Constants used for reading values from the config file. */
public final class SwerveConstants {
    // utility class
    private SwerveConstants() {}

    // FIXME: have these be passed in from Drive (via SwerveConfig).  Many of these are
    // passed into Odometry.  (Swerve and Odometry code need to be renconciled.)

    public static final double DRIVE_GEAR_RATIO = 6.75; // L2 gear ratio configuration

    public static final double STEER_GEAR_RATIO = 150.0 / 7.0; // L2 gear ratio configuration

    /*
     * Factor that converts between motor rotations and wheel degrees
     * Multiply to convert from wheel degrees to motor rotations
     * Divide to convert from motor rotations to wheel degrees
     */
    public static final double ENCODER_CONVERSION_FACTOR =
            STEER_GEAR_RATIO /*steering gear ratio*/ * (1. / 360.0) /*degrees to motor rotations*/;

    // Radius of the wheels. The circumference was measured to be 30.5cm, then experimentally this
    // value had
    // an error of 2.888%. This was then converted to meters, and then the radius.
    public static final double WHEEL_RADIUS = 30.5 * 1.02888 / 100 / (2 * Math.PI);

    /*
     * Factor that converts between drive motor angular speed (rad/s) to drive wheel tip speed (m/s)
     * Multiply to convert from wheel tip speed to motor angular speed
     * Divide to convert from angular speed to wheel tip speed
     */
    public static final double MOTOR_WHEEL_FACTOR_MPS =
            1.
                    / WHEEL_RADIUS // Wheel radians/sec
                    * DRIVE_GEAR_RATIO // Motor radians/sec
                    / (2 * Math.PI); // Motor rotations/sec (what velocity mode takes));

    public static final double WHEEL_COEFF_FRICTION_STATIC = 1.1;
    public static final double WHEEL_COEFF_FRICTION_DYNAMIC = 0.8;

    public static final double NUM_WHEELS = 4;

    // TUNE THESE!
    public static final double DRIVE_STATOR_CURRENT_LIMIT = 80.0;
    public static final double STEER_STATOR_CURRENT_LIMIT = 80.0;
}
