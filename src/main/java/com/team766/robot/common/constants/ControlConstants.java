package com.team766.robot.common.constants;

import com.team766.controllers.PIDController;

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
     * Positional velocity of robot that max joystick power controls in m/s
     */
    public static final double MAX_POSITIONAL_VELOCITY = 4.0;

    /**
     * Rotational velocity of robot that max joystick power controls in rad/s
     */
    public static final double MAX_ROTATIONAL_VELOCITY = 2.0;


    // FIXME: tune values
    public static final double DEFAULT_ROTATION_P = 0.20;
    public static final double DEFAULT_ROTATION_I = 0;
    public static final double DEFAULT_ROTATION_D = 0.008;
    public static final double DEFAULT_ROTATION_FF = 0;
    public static final double DEFAULT_ROTATION_MAX_OUTPUT = 3.0;
    public static final double DEFAULT_ROTATION_THRESHOLD = 5.0;



    public static final PIDController ROTATION_PID_CONTROLLER = new PIDController(DEFAULT_ROTATION_P, DEFAULT_ROTATION_I, DEFAULT_ROTATION_D, DEFAULT_ROTATION_FF, -DEFAULT_ROTATION_MAX_OUTPUT, DEFAULT_ROTATION_MAX_OUTPUT, DEFAULT_ROTATION_THRESHOLD);
}
