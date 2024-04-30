package com.team766.robot.swerveandshoot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.constants.SwerveDriveConstants;
import com.team766.robot.swerveandshoot.mechanisms.*;
import com.team766.robot.swerveandshoot.procedures.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms here
    private TempPickerUpper tempPickerUpper;
    private TempShooter tempShooter;
    private Lights lights;
    private Drive drive;
    private NoteCamera noteDetectorCamera;
    private ForwardApriltagCamera forwardApriltagCamera;

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
        return new OI(
                drive, tempPickerUpper, tempShooter, forwardApriltagCamera, noteDetectorCamera);
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return new AutonomousMode[] {
            // Add autonomous modes here like this:
            //    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
            //
            // If your autonomous procedure has constructor arguments, you can
            // define one or more different autonomous modes with it like this:
            //    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
            //    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),

            new AutonomousMode("DoNothing", () -> new DoNothing()),
        };
    }
}
