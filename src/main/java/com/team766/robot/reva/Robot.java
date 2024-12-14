package com.team766.robot.reva;

import com.team766.framework3.AutonomousMode;
import com.team766.framework3.RuleEngine;
import com.team766.hal.RobotConfigurator3;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.NoteCamera;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.auton_routines.*;

public class Robot implements RobotConfigurator3 {
    private SwerveDrive drive;
    private ArmAndClimber superstructure;
    private Intake intake;
    private Shooter shooter;
    private NoteCamera noteCamera;
    private ForwardApriltagCamera forwardApriltagCamera;

    @Override
    public void initializeMechanisms() {
        SwerveConfig config = new SwerveConfig();
        drive = new SwerveDrive(config);
        superstructure = new ArmAndClimber();
        intake = new Intake();
        shooter = new Shooter();
        noteCamera = new NoteCamera();
        forwardApriltagCamera = new ForwardApriltagCamera();
    }

    @Override
    public RuleEngine createOI() {
        return new OI(drive, superstructure, intake, shooter);
    }

    @Override
    public RuleEngine createLights() {
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
                    () -> new ThreePieceAmpSide(drive, superstructure, shooter, intake)),
            new AutonomousMode(
                    "4p Start Amp, All Close Pieces",
                    () -> new FourPieceAmpSide(drive, superstructure, shooter, intake)),
            new AutonomousMode(
                    "2p Start Source, Bottom Midfield Piece",
                    () -> new TwoPieceMidfieldSourceSide(drive, superstructure, shooter, intake)),
            new AutonomousMode(
                    "3p Start Amp, Amp and Top Midfield Pieces",
                    () -> new ThreePieceMidfieldAmpSide(drive, superstructure, shooter, intake)),
            new AutonomousMode(
                    "3p Start Center, Amp and Center Pieces",
                    () ->
                            new ThreePieceStartCenterTopAndAmp(
                                    drive, superstructure, shooter, intake)),
            new AutonomousMode(
                    "Just Shoot Amp",
                    () -> new JustShootAmp(drive, superstructure, shooter, intake)),
        };
    }
}
