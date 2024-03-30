package com.team766.robot.reva;

import com.team766.framework.AutonomousMode;
import com.team766.robot.reva.procedures.MoveClimbersToBottom;
import com.team766.robot.reva.procedures.OneShotBackupSourceSide;
import com.team766.robot.reva.procedures.RotateAndShootNow;
import com.team766.robot.reva.procedures.ShootOnePiece;
import com.team766.robot.reva.procedures.ThreePieceAutonAmpSide;

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
                new AutonomousMode("OneShotBackupSourceSide", () -> new OneShotBackupSourceSide()),
                new AutonomousMode("ClimbersDown", () -> new MoveClimbersToBottom()),
                new AutonomousMode("ShootOnePiece", () -> new ShootOnePiece()),
                new AutonomousMode("AltThreePiece", () -> new ThreePieceAutonAmpSide()),
                new AutonomousMode("ShootNow", () -> new RotateAndShootNow()),
                new AutonomousMode("DoNothing", () -> new RotateAndShootNow())
            };
}
