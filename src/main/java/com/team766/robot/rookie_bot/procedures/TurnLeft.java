package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.rookie_bot.Robot;

public class TurnLeft extends Procedure {

    public void run (Context context) {
        context.takeOwnership(Robot.drive);

        Robot.drive.setMotorSpeed(-0.25, 0.25);

        context.waitForSeconds(0.75);

        Robot.drive.setMotorSpeed(0.0, 0.0);
    }

}