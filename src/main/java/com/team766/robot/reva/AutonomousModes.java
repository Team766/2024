package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.robot.reva.procedures.AltThreePieceAuton;
import com.team766.robot.reva.procedures.ShootNow;

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
                new AutonomousMode("AltThreePiece", () -> new AltThreePieceAuton()),
                new AutonomousMode("ShootNow", () -> new ShootNow())
            };
}
