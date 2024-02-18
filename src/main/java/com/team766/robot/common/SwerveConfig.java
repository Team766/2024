package com.team766.robot.common;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Configuration for the Swerve Drive motors on this robot.
 */
// TODO: switch from Vector2D to WPILib's Translation2D.
public record SwerveConfig(
        String canBus,
        Vector2D frontLeftLocation,
        Vector2D frontRightLocation,
        Vector2D backLeftLocation,
        Vector2D backRightLocation,
        double driveMotorCurentLimit,
        double steerMotorCurrentLimit) {}
