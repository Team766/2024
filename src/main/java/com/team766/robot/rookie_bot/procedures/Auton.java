package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.rookie_bot.Robot;

public class Auton extends Procedure{
   
     public void run(final Context context) {
        Robot.intake.setintakePower(0.3, 0.75);
        context.takeOwnership(Robot.drive);
        Robot.drive.setDrivePower(0.5, 0.5);
        context.waitForSeconds(2);
        Robot.drive.setDrivePower(0, 0);
        context.waitForSeconds(2.5);
        Robot.drive.setDrivePower(0.25, -0.25);
        context.waitForSeconds(0.90);
        Robot.drive.setDrivePower(0, 0);
        Robot.drive.setDrivePower(0.25, -0.25);
        context.waitForSeconds(0.90);
        Robot.drive.setDrivePower(0, 0);
        context.takeOwnership(Robot.drive);
        Robot.drive.setDrivePower(0.5, 0.5);
        context.waitForSeconds(2);
        Robot.drive.setDrivePower(0, 0);
     }
}