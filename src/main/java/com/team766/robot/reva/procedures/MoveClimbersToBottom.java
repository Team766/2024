package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class MoveClimbersToBottom extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.climber);
        Robot.climber.setPower(0.25);
        context.waitFor(() -> Robot.climber.isLeftAtBottom() && Robot.climber.isRightAtBottom());
        Robot.climber.stop();
    }
}
