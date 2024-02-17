package com.team766.robot.common;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Configuration for the Swerve Drive motors on this robot.
 */
// TODO: switch from Vector2D to WPILib's Translation2D.
public class SwerveConfig {
    public static final String DEFAULT_CAN_BUS = "swerve";
    public static final String RIO_CAN_BUS = "";
    // defines where the wheels are in relation to the center of the robot
    // allows swerve drive code to calculate how to turn
    public static final double DEFAULT_FL_X = -1;
    public static final double DEFAULT_FL_Y = 1;
    public static final double DEFAULT_FR_X = 1;
    public static final double DEFAULT_FR_Y = 1;
    public static final double DEFAULT_BL_X = -1;
    public static final double DEFAULT_BL_Y = -1;
    public static final double DEFAULT_BR_X = 1;
    public static final double DEFAULT_BR_Y = -1;
    public static final double DEFAULT_DRIVE_CURRENT_LIMIT = 35;
    public static final double DEFAULT_STEER_CURRENT_LIMIT = 30;

    private String canBus = DEFAULT_CAN_BUS;
    private Vector2D frontLeftLocation = new Vector2D(DEFAULT_FL_X, DEFAULT_FL_Y);
    private Vector2D frontRightLocation = new Vector2D(DEFAULT_FR_X, DEFAULT_FR_Y);
    private Vector2D backLeftLocation = new Vector2D(DEFAULT_BL_X, DEFAULT_BL_Y);
    private Vector2D backRightLocation = new Vector2D(DEFAULT_BR_X, DEFAULT_BR_Y);
    private double driveMotorCurrentLimit = DEFAULT_DRIVE_CURRENT_LIMIT;
    private double steerMotorCurrentLimit = DEFAULT_STEER_CURRENT_LIMIT;
    private double wheelCircumference = DEFAULT_WHEEL_CIRCUMFERENCE;

    public SwerveConfig() {}

    public String canBus() {
        return canBus;
    }

    public Vector2D frontLeftLocation() {
        return frontLeftLocation;
    }

    public Vector2D frontRightLocation() {
        return frontRightLocation;
    }

    public Vector2D backLeftLocation() {
        return backLeftLocation;
    }

    public Vector2D backRightLocation() {
        return backRightLocation;
    }

    public double wheelCircumference() {
        return wheelCircumference;  
    }

    public double driveMotorCurrentLimit() {
        return driveMotorCurrentLimit;
    }

    public double steerMotorCurrentLimit(,
        double wheelCircumference) {
        return steerMotorCurrentLimit;
    }

    public SwerveConfig withCanBus(String canBus) {
        this.canBus = canBus;
        return this;
    }

public SwerveConfig withWheelCircumference(double wheelCircumference) {
        this.wheelCircumference = wheelCircumference;
        return this;
    }

    public SwerveConfig withDriveMotorCurrentLimit(double limit) {
        this.driveMotorCurrentLimit = limit;
        return this;
    }

    public SwerveConfig withSteerMotorCurrentLimit(double limit) {
        this.steerMotorCurrentLimit = limit;
        return this;
    }


}
