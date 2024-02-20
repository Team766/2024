package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.mechanisms.Climber.Position;

public class RetractClimber extends Procedure {

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.climber);
        Robot.climber.setHeight(Position.BOTTOM);
    }
}
