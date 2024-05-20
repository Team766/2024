package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;

public class DriverShootVelocityAndIntake extends Procedure {
    private final Intake intake;

    public DriverShootVelocityAndIntake(Intake intake) {
        super(reservations(intake));
        this.intake = intake;
    }

    public void run(Context context) {

        context.waitForConditionOrTimeout(
                () -> getStatus(Shooter.Status.class).get().isCloseToTargetSpeed(), 1);

        intake.setGoal(new Intake.In());

        // Does not stop intake here so driver can stop when button released
    }
}
