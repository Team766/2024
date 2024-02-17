package com.team766.robot.common.mechanisms;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Encapsulates the motors and encoders used for each physical swerve module and
 * provides driving and steering controls for each module.
 */
public class SwerveModule {
    private final String modulePlacement;
    private final MotorController drive;
    private final MotorController steer;
    private final CANcoder encoder;
    private final double wheelRadius;
    private final double offset;

    /*
     * Factor that converts between motor rotations and wheel degrees
     * Multiply to convert from wheel degrees to motor rotations
     * Divide to convert from motor rotations to wheel degrees
     */
    private static final double DRIVE_GEAR_RATIO = 6.75; // L2 gear ratio configuration
    private static final double ENCODER_CONVERSION_FACTOR =
            (150.0 / 7.0) /*steering gear ratio*/ * (1. / 360.0) /*degrees to motor rotations*/;

    /**
     * Creates a new SwerveModule.
     *
     * @param modulePlacement String description of the placement for this module, eg "FL".
     * @param drive Drive MotorController for this module.
     * @param steer Steer MotorController for this module.
     * @param encoder CANCoder for this module.
     */
    public SwerveModule(
            String modulePlacement,
            MotorController drive,
            MotorController steer,
            CANcoder encoder,
            double radius) {
        this.modulePlacement = modulePlacement;
        this.drive = drive;
        this.steer = steer;
        this.encoder = encoder;
        this.wheelRadius = radius;
        this.offset = computeEncoderOffset();
        SmartDashboard.putNumber("[" + modulePlacement + "]" + "Offset", offset);

        // Current limit for motors to avoid breaker problems
        drive.setCurrentLimit(SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT);
        steer.setCurrentLimit(SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
    }

    private double computeEncoderOffset() {
        StatusSignal<Double> value = encoder.getAbsolutePosition();
        if (!value.getStatus().isOK()) {
            Logger.get(Category.DRIVE)
                    .logData(
                            Severity.ERROR,
                            "%s unable to read encoder: %s",
                            modulePlacement,
                            value.getStatus().toString());
            return 0; // ??
        }
        return (steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR) % 360
                - (value.getValueAsDouble() * 360);
    }

    /**
     * Controls just the steer for this module.
     * Can be used to turn the wheels without moving
     * @param vector the vector specifying the module's motion
     */
    public void steer(Vector2D vector) {
        SmartDashboard.putString(
                "[" + modulePlacement + "]" + "x, y",
                String.format("%.2f, %.2f", vector.getX(), vector.getY()));

        // Calculates the angle of the vector from -180° to 180°
        final double vectorTheta = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

        // Add 360 * number of full rotations to vectorTheta, then add offset
        final double angleDegrees =
                vectorTheta
                        + 360
                                * (Math.round(
                                        (steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR
                                                        - offset
                                                        - vectorTheta)
                                                / 360))
                        + offset;

        // Sets the degree of the steer wheel
        // Needs to multiply by ENCODER_CONVERSION_FACTOR to translate into a unit the motor
        // understands
        SmartDashboard.putNumber(
                "[" + modulePlacement + "]" + "Steer", ENCODER_CONVERSION_FACTOR * angleDegrees);

        steer.set(ControlMode.Position, ENCODER_CONVERSION_FACTOR * angleDegrees);

        SmartDashboard.putNumber("[" + modulePlacement + "]" + "TargetAngle", vectorTheta);
        SmartDashboard.putNumber(
                "[" + modulePlacement + "]" + "RelativeAngle",
                steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR - offset);
        SmartDashboard.putNumber(
                "[" + modulePlacement + "]" + "CANCoder",
                encoder.getAbsolutePosition().getValueAsDouble() * 360);
    }

    /**
     * Controls both steer and power (based on the target vector) for this module.
     * @param vector the vector specifying the module's velocity in m/s and direction
     */
    public void driveAndSteer(Vector2D vector) {
        // apply the steer
        steer(vector);

        // sets the power to the magnitude of the vector
        // TODO: does this need to be clamped to a specific range, eg btn -1 and 1?
        SmartDashboard.putNumber("[" + modulePlacement + "]" + "Desired drive", vector.getNorm());
        double power = vector.getNorm() // Desired speed m/sec
                        / wheelRadius // Wheel radians/sec
                        * DRIVE_GEAR_RATIO // Motor radians/sec
                        / (2 * Math.PI); // Motor rotations/sec (what velocity mode takes)
        SmartDashboard.putNumber("[" + modulePlacement + "]" + "Input motor velocity", power);
        drive.set(ControlMode.Velocity, power);
    }

    /**
     * Stops the drive motor for this module.
     */
    public void stopDrive() {
        drive.stopMotor();
    }
}
