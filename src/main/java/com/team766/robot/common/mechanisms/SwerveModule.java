package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.SwerveConstants.*;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.reva.mechanisms.MotorUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
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
    private final double offset;

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
            double driveMotorCurrentLimit,
            double steerMotorCurrentLimit) {
        this.modulePlacement = modulePlacement;
        this.drive = drive;
        this.steer = steer;
        this.encoder = encoder;
        this.offset = computeEncoderOffset();
        // SmartDashboard.putNumber("[" + modulePlacement + "]" + "Offset", offset);

        // Current limit for motors to avoid breaker problems
        drive.setCurrentLimit(driveMotorCurrentLimit);
        steer.setCurrentLimit(steerMotorCurrentLimit);
        // TODO: tune these values!
        MotorUtil.setTalonFXStatorCurrentLimit(drive, DRIVE_STATOR_CURRENT_LIMIT);
        MotorUtil.setTalonFXStatorCurrentLimit(steer, STEER_STATOR_CURRENT_LIMIT);
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
        boolean reversed = false;
        // SmartDashboard.putString(
        //         "[" + modulePlacement + "]" + "x, y",
        //         String.format("%.2f, %.2f", vector.getX(), vector.getY()));

        // Calculates the angle of the vector from -180° to 180°
        final double vectorTheta = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

        // Add 360 * number of full rotations to vectorTheta, then add offset
        double realAngleDegrees =
                vectorTheta
                        + 360
                                * (Math.round(
                                        (steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR
                                                        - offset
                                                        - vectorTheta)
                                                / 360))
                        + offset;
        // double degreeChange =
        //         realAngleDegrees - (steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR);
        // checks if it would be more efficient to move the wheel in the opposite direction
        // if (degreeChange > 90) {
        //     realAngleDegrees -= 180;
        //     reversed = true;
        // } else if (degreeChange < -90) {
        //     realAngleDegrees += 180;
        //     reversed = true;
        // } else {
        //     reversed = false;
        // }
        final double angleDegrees = realAngleDegrees;

        // Sets the degree of the steer wheel
        // Needs to multiply by ENCODER_CONVERSION_FACTOR to translate into a unit the motor
        // understands
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + "Steer", ENCODER_CONVERSION_FACTOR * angleDegrees);

        steer.set(ControlMode.Position, ENCODER_CONVERSION_FACTOR * angleDegrees);

        // SmartDashboard.putNumber("[" + modulePlacement + "]" + "TargetAngle", vectorTheta);
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + "RelativeAngle",
        //         steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR - offset);
        SmartDashboard.putNumber(
                "[" + modulePlacement + "]" + "CANCoder",
                encoder.getAbsolutePosition().getValueAsDouble() * 360);
        // return reversed;
    }

    /**
     * Controls both steer and power (based on the target vector) for this module.
     * @param vector the vector specifying the module's velocity in m/s and direction
     */
    public void driveAndSteer(Vector2D vector) {
        // apply the steer
        steer(vector);

        // sets the power to the magnitude of the vector and reverses power if necessary
        // TODO: does this need to be clamped to a specific range, eg btn -1 and 1?
        // SmartDashboard.putNumber("[" + modulePlacement + "]" + "Desired drive",
        // vector.getNorm());
        double power;
        // if (reversed) {
        //    power = -vector.getNorm() * MOTOR_WHEEL_FACTOR_MPS;
        //    reversed = false;

        // } else {
        power = vector.getNorm() * MOTOR_WHEEL_FACTOR_MPS;
        // }
        SmartDashboard.putNumber("[" + modulePlacement + "]" + "Input motor velocity", power);
        drive.set(ControlMode.Velocity, power);

        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + "Read Vel", drive.getSensorVelocity());
    }

    /**
     * Stops the drive motor for this module.
     */
    public void stopDrive() {
        drive.stopMotor();
    }

    public SwerveModuleState getModuleState() {
        return new SwerveModuleState(
                drive.getSensorVelocity() / MOTOR_WHEEL_FACTOR_MPS,
                Rotation2d.fromDegrees(
                        steer.getSensorPosition() / ENCODER_CONVERSION_FACTOR - offset));
    }

    public void dashboardCurrentUsage() {
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + " steer supply current",
        //         MotorUtil.getCurrentUsage(steer));
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + " steer stator current",
        //         MotorUtil.getStatorCurrentUsage(steer));
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + " drive supply current",
        //         MotorUtil.getCurrentUsage(drive));
        // SmartDashboard.putNumber(
        //         "[" + modulePlacement + "]" + " drive stator current",
        //         MotorUtil.getStatorCurrentUsage(drive));
    }
}
