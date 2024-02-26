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
    public static final double DEFAULT_FL_X = 1;
    public static final double DEFAULT_FL_Y = 1;
    public static final double DEFAULT_FR_X = 1;
    public static final double DEFAULT_FR_Y = -1;
    public static final double DEFAULT_BL_X = -1;
    public static final double DEFAULT_BL_Y = 1;
    public static final double DEFAULT_BR_X = -1;
    public static final double DEFAULT_BR_Y = -1;
    // Circumference of the wheels. It was measured to be 30.5cm, then experimentally this value had
    // an error of 2.888%. This was then converted to meters.
    public static final double DEFAULT_WHEEL_CIRCUMFERENCE = 30.5 * 1.02888 / 100;
    // Unique to the type of swerve module we have. This is the factor converting motor revolutions
    // to wheel revolutions.
    public static final double DEFAULT_GEAR_RATIO = 6.75;
    // The distance between the center of a wheel to the center of an adjacent wheel, assuming the
    // robot is square. This was measured as 20.5 inches, then converted to meters.
    public static final double DEFAULT_DISTANCE_BETWEEN_WHEELS = 20.5 * 2.54 / 100;
    // Unique to the type of swerve module we have. For every revolution of the wheel, the encoders
    // will increase by 1.
    public static final int DEFAULT_ENCODER_TO_REVOLUTION_CONSTANT = 1;
    public static final double DEFAULT_DRIVE_CURRENT_LIMIT = 35;
    public static final double DEFAULT_STEER_CURRENT_LIMIT = 30;

    private String canBus = DEFAULT_CAN_BUS;
    // TODO: can we combine Drive's wheel locations and odometry's wheel locations?
    private Vector2D frontLeftLocation = new Vector2D(DEFAULT_FL_X, DEFAULT_FL_Y);
    private Vector2D frontRightLocation = new Vector2D(DEFAULT_FR_X, DEFAULT_FR_Y);
    private Vector2D backLeftLocation = new Vector2D(DEFAULT_BL_X, DEFAULT_BL_Y);
    private Vector2D backRightLocation = new Vector2D(DEFAULT_BR_X, DEFAULT_BR_Y);
    private double wheelCircumference = DEFAULT_WHEEL_CIRCUMFERENCE;
    private double gearRatio = DEFAULT_GEAR_RATIO;
    private double distanceBetweenWheels = DEFAULT_DISTANCE_BETWEEN_WHEELS;
    private double wheelDistanceFromCenter = Math.sqrt(2) * DEFAULT_DISTANCE_BETWEEN_WHEELS / 2;
    private int encoderToRevolutionConstant = DEFAULT_ENCODER_TO_REVOLUTION_CONSTANT;
    private double driveMotorCurrentLimit = DEFAULT_DRIVE_CURRENT_LIMIT;
    private double steerMotorCurrentLimit = DEFAULT_STEER_CURRENT_LIMIT;

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

    public double gearRatio() {
        return gearRatio;
    }

    public double distanceBetweenWheels() {
        return distanceBetweenWheels;
    }

    public double wheelDistanceFromCenter() {
        return wheelDistanceFromCenter;
    }

    public int encoderToRevolutionConstant() {
        return encoderToRevolutionConstant;
    }

    public double driveMotorCurrentLimit() {
        return driveMotorCurrentLimit;
    }

    public double steerMotorCurrentLimit() {
        return steerMotorCurrentLimit;
    }

    public SwerveConfig withCanBus(String canBus) {
        this.canBus = canBus;
        return this;
    }

    public SwerveConfig withFrontLeftLocation(double x, double y) {
        this.frontLeftLocation = new Vector2D(x, y);
        return this;
    }

    public SwerveConfig withFrontRightLocation(double x, double y) {
        this.frontRightLocation = new Vector2D(x, y);
        return this;
    }

    public SwerveConfig withBackLeftLocation(double x, double y) {
        this.backLeftLocation = new Vector2D(x, y);
        return this;
    }

    public SwerveConfig withBackRightLocation(double x, double y) {
        this.backRightLocation = new Vector2D(x, y);
        return this;
    }

    public SwerveConfig withWheelCircumference(double wheelCircumference) {
        this.wheelCircumference = wheelCircumference;
        return this;
    }

    public SwerveConfig withGearRatio(double gearRatio) {
        this.gearRatio = gearRatio;
        return this;
    }

    public SwerveConfig withEncoderToRevolutionConstant(int encoderToRevolutionConstant) {
        this.encoderToRevolutionConstant = encoderToRevolutionConstant;
        return this;
    }

    public SwerveConfig withDistanceBetweenWheels(double distanceBetweenWheels) {
        this.distanceBetweenWheels = distanceBetweenWheels;
        this.wheelDistanceFromCenter = Math.sqrt(2) * distanceBetweenWheels / 2;
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
