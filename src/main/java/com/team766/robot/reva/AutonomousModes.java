package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.robot.reva.procedures.auton_routines.FourPieceAmpSide;
import com.team766.robot.reva.procedures.auton_routines.JustShootAmp;
import com.team766.robot.reva.procedures.auton_routines.ThreePieceAmpSide;
import com.team766.robot.reva.procedures.auton_routines.ThreePieceMidfieldAmpSide;
import com.team766.robot.reva.procedures.auton_routines.ThreePieceStartCenterTopAndAmp;
import com.team766.robot.reva.procedures.auton_routines.TwoPieceMidfieldSourceSide;

public class AutonomousModes {
    public static final AutonomousMode[] AUTONOMOUS_MODES =
            new AutonomousMode[] {
                // Add autonomous modes here like this:
                //    new AutonomousMode("NameOfAutonomousMode", () -> new MyAutonomousProcedure()),
                //
                // If your autonomous procedure has constructor arguments, you can
                // define one or more different autonomous modes with it like this:
                //    new AutonomousMode("DriveFast", () -> new DriveStraight(1.0)),
                //    new AutonomousMode("DriveSlow", () -> new DriveStraight(0.4)),
                new AutonomousMode(
                        "3p Start Amp, Amp and Center Pieces", () -> new ThreePieceAmpSide()),
                new AutonomousMode("4p Start Amp, All Close Pieces", () -> new FourPieceAmpSide()),
                new AutonomousMode(
                        "2p Start Source, Bottom Midfield Piece",
                        () -> new TwoPieceMidfieldSourceSide()),
                new AutonomousMode(
                        "3p Start Amp, Amp and Top Midfield Pieces",
                        () -> new ThreePieceMidfieldAmpSide()),
                new AutonomousMode(
                        "3p Start Center, Amp and Center Pieces",
                        () -> new ThreePieceStartCenterTopAndAmp()),
                new AutonomousMode("Just Shoot Amp", () -> new JustShootAmp())
            };
}
