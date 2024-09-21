package com.team766.robot.common.constants;

public class ControlConstants {

    // Amount to reduce driving power to when holding the fine driving button
    public static final double FINE_DRIVING_COEFFICIENT = 0.25;

    // Value below which the joystick movement does not register
    public static final double JOYSTICK_DEADZONE = 0.05;

    // Exponent giving joystick curved power mapping for translational movement
    public static final double TRANSLATIONAL_CURVE_POWER = 1.0;

    // Exponent giving joystick curved power mapping for rotational movement
    public static final double ROTATIONAL_CURVE_POWER = 1.0;

    /**
     * Translational velocity of robot that max joystick power controls in m/s
     */
    public static final double MAX_TRANSLATIONAL_VELOCITY = 5.0;

    /**
     * m/s
     */
    public static final double AT_TRANSLATIONAL_SPEED_THRESHOLD =
            0.5; // TODO(MF3): Find actual value

    /**
     * Rotational velocity of robot that max joystick power controls in rad/s
     */
    public static final double MAX_ROTATIONAL_VELOCITY = 4.0;

    /**
     * degrees
     */
    public static final double AT_ROTATIONAL_ANGLE_THRESHOLD = 3.0; // TODO(MF3): Find actual value

    /**
     * degrees/second
     */
    public static final double AT_ROTATIONAL_SPEED_THRESHOLD = 3.0; // TODO(MF3): Find actual value
}
