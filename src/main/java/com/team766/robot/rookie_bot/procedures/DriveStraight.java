package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.rookie_bot.Robot;

public class DriveStraight extends Procedure{
    public void run(Context context) {
        Robot.drive.setMotorSpeed(0.1, 0.1);
        context.waitForSeconds(7.5);
        Robot.drive.setMotorSpeed(0, 0);
    }
    
}
