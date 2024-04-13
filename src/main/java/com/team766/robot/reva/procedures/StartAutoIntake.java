package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class StartAutoIntake extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.shoulder);
        Robot.shoulder.rotate(ShoulderPosition.BOTTOM);
        context.releaseOwnership(Robot.shoulder);
        context.waitForConditionOrTimeout(Robot.shoulder::isFinished, 1.5);
        context.startAsync(new IntakeUntilIn());
    }
}
