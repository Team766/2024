package com.team766.robot.gatorade.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 *
 */
public final class OdometryInputConstants {

    // Circumference of the wheels. It was measured to be 30.5cm, then experimentally this value had
    // an error of 2.888%. This was then converted to meters.
    public static final double WHEEL_CIRCUMFERENCE = 30.5 * 1.02888 / 100;
    // Unique to the type of swerve module we have. This is the factor converting motor revolutions
    // to wheel revolutions.
    public static final double GEAR_RATIO = 6.75;
    // Unique to the type of swerve module we have. For every revolution of the wheel, the encoders
    // will increase by 1.
    public static final int ENCODER_TO_REVOLUTION_CONSTANT = 1;
    // The distance between the center of a wheel to the center of an adjacent wheel, assuming the
    // robot. This was measured
    // as 20.5 inches, then converted to meters.
    public static final double DISTANCE_BETWEEN_WHEELS = 20.5 * 2.54 / 100;
    // The distance between the center of the robot to a given wheel, assuming the robot is square
    public static final double WHEEL_DIST_FROM_CENTER = Math.sqrt(2) * DISTANCE_BETWEEN_WHEELS / 2;
    // How often odometry updates, in seconds.
    public static final double RATE_LIMITER_TIME = 0.05;
}
