package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.amp_side_auto.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v1.source_side_auto.*;
import com.team766.robot.reva.procedures.auton_routines.autons_v2.*;

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
                /* AutonomousMode(
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
                new AutonomousMode("3p Start Amp, Amp and Center Pieces, run away", () -> new Amp3NotesAutoncloseCrazy()),
                new AutonomousMode("4p Start Amp, 1 Close and 2 Midfield Amp Side Pieces", () -> new Amp4NoteAuton1close1top1midfarCrazy()),
                new AutonomousMode("4p Start Amp, 1 Close and 2 Midfield Amp Side and Under Stage Pieces", () -> new Amp4NoteAuton1close2farCrazy()),
                new AutonomousMode("4p Start Amp, 4 close", () -> new Amp4NotesAutoncloseCrazy()),
                new AutonomousMode("1p run away", () -> new OneNoteAutoMoveOutCrazy()),
                new AutonomousMode("5p center start", () -> new FivePieceMidStart()),
                new AutonomousMode("4p close center start", () -> new FourPieceMidStart()),
                new AutonomousMode("5p source start", () -> new FivePieceSourceSide()),
                */
                new AutonomousMode("Just Shoot Amp", () -> new JustShootAmp()),
                new AutonomousMode("Just Shoot Source", () -> new JustShootSource()),
                new AutonomousMode("Just Shoot Center", () -> new JustShootCenter()),
                new AutonomousMode("3p Start Amp, 1,4", () -> new Amp14Auto()),
                new AutonomousMode("3p Start Amp, 1,5 grab 4", () -> new Amp15Grab4Auto()),
                new AutonomousMode("3p Start Amp, 1,5 pass 4", () -> new Amp15Pass4Auto()),
                new AutonomousMode("4p Start Center, 1,2,3 grab 6", () -> new Center123Grab6Auto()),
                new AutonomousMode("4p Start Center, 2,3,6", () -> new Center236Auto()),
                new AutonomousMode("4p Start Center, 2,3,6 VISION", () -> new Center236VisionAuto()),
                new AutonomousMode("5p Start Center, 1,2,3,5 VISION", () -> new Center1235VisionAuto()),
                new AutonomousMode("5p Start Center, 1,2,3,6 VISION", () -> new Center1236VisionAuto()),
                new AutonomousMode("2p Start Source, 8", () -> new Source8Auto()),
                new AutonomousMode("2p Start Source, 8 grab 7", () -> new Source8Grab7Auto()),
                new AutonomousMode("1p Start Source, BUMP MIDFIELD", () -> new SourceGrab8BumpMidAuto())
            };
}
