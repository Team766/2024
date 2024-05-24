package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

public class StartAutoIntake extends Procedure {
    private final Superstructure superstructure;
    private final Intake intake;

    public StartAutoIntake(Superstructure superstructure, Intake intake) {
        super(reservations(superstructure, intake));
        this.superstructure = superstructure;
        this.intake = intake;
    }

    public void run(Context context) {
        final var armTarget = Shoulder.RotateToPosition.BOTTOM;
        superstructure.setGoal(armTarget);
        context.waitForConditionOrTimeout(
                () -> getStatus(Shoulder.Status.class).get().isNearTo(armTarget), 1.5);
        intake.setGoal(new Intake.SetPowerForSensorDistance());
    }
}
