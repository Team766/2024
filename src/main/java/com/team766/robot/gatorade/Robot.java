package com.team766.robot.gatorade;

import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import com.team766.robot.gatorade.mechanisms.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Robot {
    // Declare mechanisms here
    public static Intake intake;
    public static Wrist wrist;
    public static Elevator elevator;
    public static Shoulder shoulder;
    public static Drive drive;
    public static Lights lights;

    public static void robotInit() {
        // Initialize mechanisms here
        SwerveConfig config =
                new SwerveConfig(
                        SwerveDriveConstants.SWERVE_CANBUS,
                        new Vector2D(SwerveDriveConstants.FL_X, SwerveDriveConstants.FL_Y),
                        new Vector2D(SwerveDriveConstants.FR_X, SwerveDriveConstants.FR_Y),
                        new Vector2D(SwerveDriveConstants.BL_X, SwerveDriveConstants.BL_Y),
                        new Vector2D(SwerveDriveConstants.BR_X, SwerveDriveConstants.BR_Y),
                        SwerveDriveConstants.DRIVE_MOTOR_CURRENT_LIMIT,
                        SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
        intake = new Intake();
        wrist = new Wrist();
        elevator = new Elevator();
        shoulder = new Shoulder();
        drive = new Drive(config);
        lights = new Lights();
    }
}
