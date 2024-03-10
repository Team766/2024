package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class IntakeUntilIn extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        while(!Robot.intake.hasNoteInIntake()) {
            Robot.intake.setIntakePowerForSensorDistance();
            context.yield();
        }
    }
}
