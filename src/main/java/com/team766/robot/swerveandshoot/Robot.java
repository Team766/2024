package com.team766.robot.swerveandshoot;

import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.constants.SwerveDriveConstants;
import com.team766.robot.swerveandshoot.mechanisms.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Robot implements RobotConfigurator {
    // Declare mechanisms here
    public static TempPickerUpper tempPickerUpper;
    public static TempShooter tempShooter;
    public static NoteUtil noteUtil;
    public static Lights lights;
    public static Drive drive;
    public static SpeakerShooterPowerCalculator speakerShooter;

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
                        SwerveDriveConstants.STEER_MOTOR_CURRENT_LIMIT);
        tempPickerUpper = new TempPickerUpper();
        tempShooter = new TempShooter();
        noteUtil = new NoteUtil();
        lights = new Lights();
        drive = new Drive(config);

        try {
            speakerShooter = new SpeakerShooterPowerCalculator();
        } catch (AprilTagGeneralCheckedException e) {
            // Logger.logRaw(Severity.ERROR, "Couldn't find drive team alliance color to initialize
            // the speaker shooter.");
        }
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
