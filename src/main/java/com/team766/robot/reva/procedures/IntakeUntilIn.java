package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class IntakeUntilIn extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        Robot.lights.signalNoNoteInIntakeYet();
        while (!Robot.intake.hasNoteInIntake()) {
            Robot.intake.setIntakePowerForSensorDistance();

            if (Robot.intake.isNoteClose()) {
                Robot.lights.signalNoteInIntake();
            }
            context.yield();
        }

        context.releaseOwnership(Robot.intake);

        Robot.lights.signalNoteInIntake();

        context.waitForSeconds(2);

        Robot.lights.turnLightsOff();
    }
}
