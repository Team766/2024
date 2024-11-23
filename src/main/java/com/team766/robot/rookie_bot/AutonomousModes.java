package com.team766.robot.rookie_bot;

import com.team766.framework.AutonomousMode;
import com.team766.robot.rookie_bot.procedures.*;

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
                new AutonomousMode("Auton", () -> new Auton()),
            };
        

}
