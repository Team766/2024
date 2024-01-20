package com.team766.robot.swerveandshoot.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 *
 * Starter set of constants.  Customize and update based on joystick and boxop controls.
 */
public final class InputConstants {

    // joysticks
    public static final int LEFT_JOYSTICK = 0;
    public static final int RIGHT_JOYSTICK = 1;
    public static final int BOXOP_GAMEPAD = 2; // should be in Logitech Mode

    // navigation
    public static final int AXIS_LEFT_RIGHT = 0;
    public static final int AXIS_FORWARD_BACKWARD = 1;
    public static final int AXIS_TWIST = 3;

    // buttons
    public static final int BUTTON_FINE_DRIVING = 1;
    public static final int BUTTON_CROSS_WHEELS = 3;
    public static final int BUTTON_RESET_GYRO = 9;
    public static final int BUTTON_RESET_POS = 15;
}
