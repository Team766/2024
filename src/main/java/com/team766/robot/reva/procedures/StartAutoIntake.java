package com.team766.robot.reva.procedures;

import static com.team766.framework3.Conditions.waitForRequestOrTimeout;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shoulder;

public class StartAutoIntake extends Procedure {
    private final ArmAndClimber superstructure;
    private final Intake intake;

    public StartAutoIntake(ArmAndClimber superstructure, Intake intake) {
        this.superstructure = reserve(superstructure);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        final var armTarget = Shoulder.RotateToPosition.BOTTOM;
        superstructure.setRequest(armTarget);
        waitForRequestOrTimeout(context, armTarget, 1.5);
        intake.setRequest(new Intake.SetPowerForSensorDistance());
    }
}
