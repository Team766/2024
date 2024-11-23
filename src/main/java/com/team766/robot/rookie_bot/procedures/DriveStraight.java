package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Procedure;
import com.team766.framework.Context;
import com.team766.robot.rookie_bot.Robot;

public class DriveStraight extends Procedure {
    public void run(Context context) {
        Robot.drive.drive(0.5, 0);
        context.waitForSeconds(3);
        Robot.drive.drive(0, 0);
    }
}
