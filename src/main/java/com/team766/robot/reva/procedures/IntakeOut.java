package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class IntakeOut extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        Robot.intake.out();
    }
}
