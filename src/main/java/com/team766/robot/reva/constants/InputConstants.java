package com.team766.robot.reva.constants;

/**
 * Constants used for the Operator Interface, eg for joyticks, buttons, dials, etc.
 */
public final class InputConstants {

    // Joysticks
    public static final int LEFT_JOYSTICK = 0;
    public static final int RIGHT_JOYSTICK = 1;
    public static final int MACROPAD = 2;
    public static final int BOXOP_GAMEPAD_L = 3; // should be in Logitech Mode
    public static final int BOXOP_GAMEPAD_X = 4; // xbox

    // Macropad buttons
    public static final int CONTROL_SHOULDER = 1;
    public static final int CONTROL_SHOOTER = 2;
    public static final int CONTROL_LEFT_CLIMBER = 3;
    public static final int CONTROL_RIGHT_CLIMBER = 4;
    public static final int INTAKE_IN = 5;
    public static final int INTAKE_OUT = 6;
    public static final int NUDGE_UP = 8;
    public static final int NUDGE_DOWN = 12;
    public static final int MACROPAD_PRESET_1 = 13;
    public static final int MACROPAD_PRESET_2 = 14;
    public static final int MACROPAD_PRESET_3 = 15;
    public static final int RESET_CLIMBER_ENCODERS = 16;

    // Xbox buttons
    // TODO: change
    public static final int XBOX_A = 1;
    public static final int XBOX_B = 2;
    public static final int XBOX_X = 3;
    public static final int XBOX_Y = 4;
    public static final int XBOX_LB = 5;
    public static final int XBOX_RB = 6;
    public static final int XBOX_LT = 2;
    public static final int XBOX_RT = 3;

    // Xbox axes
    public static final int XBOX_LS_X = 0;
    public static final int XBOX_LS_Y = 1;
    public static final int XBOX_RS_X = 4;
    public static final int XBOX_RS_Y = 5;
    public static final double XBOX_DEADZONE = 0.05;

    public static final int POV_UP = 0;
    public static final int POV_DOWN = 180;
}
