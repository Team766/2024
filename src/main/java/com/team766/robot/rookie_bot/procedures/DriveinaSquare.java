package com.team766.robot.rookie_bot.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class DriveinaSquare extends Procedure {
    public void run(Context context) {
        // This loop repeats 4 times
        for (int i = 0; i < 4; ++i) {
            // Drive along the side of the square
            new DriveStraight().run(context);

            // Turn at corner
            new TurnRight().run(context);
        }
    }
}
