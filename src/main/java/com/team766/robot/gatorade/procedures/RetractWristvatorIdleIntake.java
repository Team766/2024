package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;

public class RetractWristvatorIdleIntake extends Procedure {
    public void run(Context context) {
        context.runSync(new RetractWristvator());
        context.runSync(new IntakeIdle());
    }
}
