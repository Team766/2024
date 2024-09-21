package com.team766.robot.gatorade.constants;

public class ControlConstants {

    // Amount to reduce driving power to when holding the fine driving button
    public static final double FINE_DRIVING_COEFFICIENT = 0.25;

    // Value below which the joystick movement does not register
    public static final double JOYSTICK_DEADZONE = 0.025;

    // Translational velocity of robot that max joystick power controls in m/s
    public static final double MAX_VEL_TRANS = 6.0;

    // Rotational velocity of robot that max joystick power controls in rad/s
    public static final double MAX_VEL_ROT = 6.0;
}
