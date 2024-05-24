package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.framework.LightsBase;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.NoteCamera;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.auton_routines.*;

public class Robot implements RobotConfigurator {
    @Override
    public void initializeSubsystems() {
        SwerveConfig config = new SwerveConfig();
        addSubsystem(new Drive(config));
        addSubsystem(new Superstructure());
        addSubsystem(new Intake());
        addSubsystem(new Shooter());
        addSubsystem(new NoteCamera());
        addSubsystem(new ForwardApriltagCamera());
    }

    @Override
    public OI createOI() {
        return new OI();
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
                    (Drive drive, Superstructure ss, Shooter shooter, Intake intake) ->
                            new ThreePieceAmpSide(drive, ss, shooter, intake)),
            new AutonomousMode(
                    "4p Start Amp, All Close Pieces",
                    (Drive drive, Superstructure ss, Shooter shooter, Intake intake) ->
                            new FourPieceAmpSide(drive, ss, shooter, intake)),
            new AutonomousMode(
                    "2p Start Source, Bottom Midfield Piece",
                    (Drive drive, Superstructure ss, Shooter shooter, Intake intake) ->
                            new TwoPieceMidfieldSourceSide(drive, ss, shooter, intake)),
            new AutonomousMode(
                    "3p Start Amp, Amp and Top Midfield Pieces",
                    (Drive drive, Superstructure ss, Shooter shooter, Intake intake) ->
                            new ThreePieceMidfieldAmpSide(drive, ss, shooter, intake)),
            new AutonomousMode(
                    "3p Start Center, Amp and Center Pieces",
                    (Drive drive, Superstructure ss, Shooter shooter, Intake intake) ->
                            new ThreePieceStartCenterTopAndAmp(drive, ss, shooter, intake))
        };
    }
}
