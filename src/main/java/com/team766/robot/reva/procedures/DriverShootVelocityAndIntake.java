package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;

public class DriverShootVelocityAndIntake extends Procedure {
    private final Shooter shooter;
    private final Intake intake;

    public DriverShootVelocityAndIntake(Shooter shooter, Intake intake) {
        super(reservations(shooter, intake));
        this.shooter = shooter;
        this.intake = intake;
    }

    public void run(Context context) {

        context.waitForConditionOrTimeout(shooter::isCloseToExpectedSpeed, 1);

        context.runSync(new IntakeIn(intake));

        // Does not stop intake here so driver can stop when button released
    }
}
