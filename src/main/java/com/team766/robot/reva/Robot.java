package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.SwerveDriveConstants;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shoulder;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    public static Intake intake;
    public static Drive drive;
    public static Shoulder shoulder;

    @Override
    public void initializeMechanisms() {
        SwerveConfig config =
                new SwerveConfig(
                        SwerveDriveConstants.SWERVE_CANBUS,
                        new Vector2D(SwerveDriveConstants.FL_X, SwerveDriveConstants.FL_Y),
                        new Vector2D(SwerveDriveConstants.FR_X, SwerveDriveConstants.FR_Y),
                        new Vector2D(SwerveDriveConstants.BL_X, SwerveDriveConstants.BL_Y),
                        new Vector2D(SwerveDriveConstants.BR_X, SwerveDriveConstants.BR_Y),
                        SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT,
                        SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT,
                        SwerveDriveConstants.WHEEL_CIRCUMFERENCE);
        drive = new Drive(config);
        shoulder = new Shoulder();
        intake = new Intake();
    }

    @Override
    public Procedure createOI() {
        return new OI();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
