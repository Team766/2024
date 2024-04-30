package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class StartAutoIntake extends Procedure {
    private final Shoulder shoulder;
    private final Intake intake;
    private final Lights lights;

    public StartAutoIntake(Shoulder shoulder, Intake intake, Lights lights) {
        super(reservations(shoulder, intake));
        this.shoulder = shoulder;
        this.intake = intake;
        this.lights = lights;
    }

    public void run(Context context) {
        shoulder.rotate(ShoulderPosition.BOTTOM);
        context.waitForConditionOrTimeout(shoulder::isFinished, 1.5);
        context.startAsync(new IntakeUntilIn(intake, lights));
    }
}
