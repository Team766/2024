package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.rookie_bot.Robot;

public class DriveStraight extends Procedure {

    public void run(final Context context) {
        context.takeOwnership(Robot.drive);
        Robot.drive.setDrivePower(0.5, 0.5);
        context.waitForSeconds(2);
        Robot.drive.setDrivePower(0, 0);
    }
}
