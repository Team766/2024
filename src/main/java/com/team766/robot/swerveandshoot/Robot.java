package com.team766.robot.swerveandshoot;

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
    public static Lights lights;
    public static Drive drive;
    public static NoteCamera noteDetectorCamera;
    public static ForwardApriltagCamera forwardApriltagCamera;

    @Override
    public void initializeMechanisms() {
        SwerveConfig config = new SwerveConfig().withCanBus(SwerveDriveConstants.SWERVE_CANBUS);
        tempPickerUpper = new TempPickerUpper();
        tempShooter = new TempShooter();
        lights = new Lights();
        drive = new Drive(config);
        noteDetectorCamera = new NoteCamera();
        forwardApriltagCamera = new ForwardApriltagCamera();
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
