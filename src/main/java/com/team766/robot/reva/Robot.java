package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.framework.LightsBase;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.NoteCamera;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.auton_routines.*;

public class Robot implements RobotConfigurator {
    // Declare mechanisms (as static fields) here
    private Drive drive;
    private Climber climber;
    private Shoulder shoulder;
    private Intake intake;
    private Shooter shooter;
    // not yet initialized, until we have the camera on the robot and test it.
    private ForwardApriltagCamera forwardApriltagCamera;
    private NoteCamera noteCamera;

    @Override
    public void initializeMechanisms() {
        SwerveConfig config = new SwerveConfig();
        drive = new Drive(config);
        climber = new Climber();
        shoulder = new Shoulder();
        intake = new Intake();
        shooter = new Shooter();
        noteCamera = new NoteCamera();
        forwardApriltagCamera = new ForwardApriltagCamera();
    }

    @Override
    public OI createOI() {
        return new OI(drive, shoulder, shooter, intake, climber, forwardApriltagCamera);
    }

    @Override
    public LightsBase createLights() {
        return new Lights();
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
            new AutonomousMode(
                    "3p Start Amp, Amp and Center Pieces",
                    () -> new ThreePieceAmpSide(
                            drive, shoulder, shooter, intake, climber, forwardApriltagCamera)),
            new AutonomousMode(
                    "4p Start Amp, All Close Pieces",
                    () -> new FourPieceAmpSide(
                            drive, shoulder, shooter, intake, climber, forwardApriltagCamera)),
            new AutonomousMode(
                    "2p Start Source, Bottom Midfield Piece",
                    () -> new TwoPieceMidfieldSourceSide(
                            drive, shoulder, shooter, intake, climber, forwardApriltagCamera)),
            new AutonomousMode(
                    "3p Start Amp, Amp and Top Midfield Pieces",
                    () -> new ThreePieceMidfieldAmpSide(
                            drive, shoulder, shooter, intake, climber, forwardApriltagCamera)),
            new AutonomousMode(
                    "3p Start Center, Amp and Center Pieces",
                    () -> new ThreePieceStartCenterTopAndAmp(
                            drive, shoulder, shooter, intake, climber, forwardApriltagCamera))
        };
    }
}
