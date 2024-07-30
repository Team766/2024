package com.team766.robot.burro_arm.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 *
 * Starter set of constants.  Customize and update based on joystick and boxop controls.
 */
public final class InputConstants {
    // navigation
    public static final int AXIS_FORWARD_BACKWARD = 1;
    public static final int AXIS_TURN = 3;

    // buttons
    public static final int BUTTON_ARM_UP = 5;
    public static final int BUTTON_ARM_DOWN = 6;
    public static final int BUTTON_INTAKE = 7;
    public static final int BUTTON_OUTTAKE = 8;

    public static final double NUDGE_UP_INCREMENT = 5.0; // degrees
    public static final double NUDGE_DOWN_INCREMENT = 5.0; // degrees
}
