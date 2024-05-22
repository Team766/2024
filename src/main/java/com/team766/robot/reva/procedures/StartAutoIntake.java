package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.RotateToPosition;

public class StartAutoIntake extends Procedure {
    private final Shoulder shoulder;
    private final Intake intake;

    public StartAutoIntake(Shoulder shoulder, Intake intake) {
        super(reservations(shoulder, intake));
        this.shoulder = shoulder;
        this.intake = intake;
    }

    public void run(Context context) {
        final var armTarget = RotateToPosition.BOTTOM;
        shoulder.setGoal(armTarget);
        context.waitForConditionOrTimeout(() -> shoulder.getStatus().isNearTo(armTarget), 1.5);
        intake.setGoal(new Intake.SetPowerForSensorDistance());
    }
}
