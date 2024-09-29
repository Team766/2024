package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.rookie_bot.Robot;

public class DriveForward extends Procedure {
    public void run(Context context) {
        Robot.drive.go(0.5, 0.5);
        context.waitForSeconds(2);
        Robot.drive.go(0,0);
    }
}
