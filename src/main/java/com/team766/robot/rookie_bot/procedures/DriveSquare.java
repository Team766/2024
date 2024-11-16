package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class DriveSquare extends Procedure {

    public void run (Context context) {
        for (int i = 0; i < 4; ++i) {
            new DriveStraight().run (context);

            new TurnRight().run(context);
        }
    }
}