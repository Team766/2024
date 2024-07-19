package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.math.StopWatch;
import com.team766.robot.reva.Robot;

public class IntakeUntilIn extends Procedure {
    public void run(Context context) {
        // StopWatch stopwatch = new StopWatch(new SystemClock());
        context.takeOwnership(Robot.intake);
        Robot.lights.signalNoNoteInIntakeYet();
        while (!Robot.intake.hasNoteInIntake()) {
            Robot.intake.setIntakePowerForSensorDistance();
            if (Robot.intake.isNoteClose()) {
                Robot.lights.signalNoteInIntake();
                // stopwatch.startIfNecessary();
            }
            context.yield();
            // if (stopwatch.elapsedSeconds() > 0.6) {
            //     Robot.intake.out();
            //     context.waitForSeconds(0.1);
            //     Robot.intake.setIntakePowerForSensorDistance();
            //     stopwatch.reset();
            // }
        }

        Robot.intake.stop();

        context.releaseOwnership(Robot.intake);

        Robot.lights.signalNoteInIntake();

        context.waitForSeconds(2);

        Robot.lights.turnLightsOff();
    }
}
