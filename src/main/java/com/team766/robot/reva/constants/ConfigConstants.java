package com.team766.robot.reva.constants;

/** Constants used for reading values from the config file. */
public final class ConfigConstants {
    // utility class
    private ConfigConstants() {}

    public static final String CLIMBER_LEFT_MOTOR = "climber.leftMotor";
    public static final String CLIMBER_RIGHT_MOTOR = "climber.rightMotor";

    // shoulder config values
    public static final String SHOULDER_RIGHT = "shoulder.rightMotor";
    public static final String SHOULDER_LEFT = "shoulder.leftMotor";
    public static final String SHOULDER_ENCODER = "shoulder.encoder";

    // intake config values
    public static final String INTAKE_MOTOR = "intake.motor";

    // shooter config values
    public static final String SHOOTER_MOTOR_TOP = "shooter.topMotor";
    public static final String SHOOTER_MOTOR_BOTTOM = "shooter.bottomMotor";
}
