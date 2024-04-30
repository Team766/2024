package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Climber;

public class MoveClimbersToBottom extends Procedure {
    private final Climber climber;

    public MoveClimbersToBottom(Climber climber) {
        super(reservations(climber));
        this.climber = climber;
    }

    public void run(Context context) {
        climber.setPower(0.25);
        context.waitFor(() -> climber.isLeftAtBottom() && climber.isRightAtBottom());
        climber.stop();
    }
}
