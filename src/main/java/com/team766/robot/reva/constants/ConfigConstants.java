package com.team766.robot.reva.constants;

/** Constants used for reading values from the config file. */
public final class ConfigConstants {
    // utility class
    private ConfigConstants() {}

    // drive config values
    public static final String DRIVE_GYRO = "drive.Gyro";
    public static final String DRIVE_DRIVE_FRONT_RIGHT = "drive.DriveFrontRight";
    public static final String DRIVE_DRIVE_FRONT_LEFT = "drive.DriveFrontLeft";
    public static final String DRIVE_DRIVE_BACK_RIGHT = "drive.DriveBackRight";
    public static final String DRIVE_DRIVE_BACK_LEFT = "drive.DriveBackLeft";

    public static final String DRIVE_STEER_FRONT_RIGHT = "drive.SteerFrontRight";
    public static final String DRIVE_STEER_FRONT_LEFT = "drive.SteerFrontLeft";
    public static final String DRIVE_STEER_BACK_RIGHT = "drive.SteerBackRight";
    public static final String DRIVE_STEER_BACK_LEFT = "drive.SteerBackLeft";

    public static final String CLIMBER_LEFT_MOTOR = "climber.LeftMotor";
    public static final String CLIMBER_RIGHT_MOTOR = "climber.RightMotor";

    public static final String SHOULDER_RIGHT = "shoulder.rightMotor";
    public static final String SHOULDER_LEFT = "shoulder.leftMotor";

    // intake config values
    public static final String INTAKE_MOTOR = "intake.motor";

    // shooter config values
    public static final String SHOOTER_MOTOR_TOP = "shooter.topMotor";
    public static final String SHOOTER_MOTOR_BOTTOM = "shooter.bottomMotor";
}
