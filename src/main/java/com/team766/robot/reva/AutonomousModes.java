package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.amp_side_auto.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.source_side_auto.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.middle_side_auto.*;

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
                new AutonomousMode(
                        "4p Start Amp, All close pieces OG",
                        () -> new FourPieceAmpSide()),
                new AutonomousMode("Just Shoot Amp", () -> new ThreePieceAmpSideCrazy()),
                new AutonomousMode("3p Start Amp, Amp and Center Pieces, run away", () -> new Amp3NotesAutoncloseCrazy()),
                new AutonomousMode("4p Start Amp, 1 Close and 2 Midfield Amp Side Pieces", () -> new Amp4NoteAuton1close1top1midfarCrazy()),
                new AutonomousMode("4p Start Amp, 1 Close and 2 Midfield Amp Side and Under Stage Pieces", () -> new Amp4NoteAuton1close2farCrazy()),
                new AutonomousMode("4p Start Amp, 4 close", () -> new Amp4NotesAutoncloseCrazy()),
                new AutonomousMode("1p run away", () -> new OneNoteAutoMoveOutCrazy()),
                new AutonomousMode("5p center start", () -> new FivePieceMidStart()),
                new AutonomousMode("4p close center start", () -> new FourPieceMidStart()),
                new AutonomousMode("5p source start", () -> new FivePieceSourceSide()),
            };
}
