package com.team766.robot.reva.procedures;

import static com.team766.framework3.Conditions.waitForStatusWithOrTimeout;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;

public class DriverShootVelocityAndIntake extends Procedure {
    private final Intake intake;

    public DriverShootVelocityAndIntake(Intake intake) {
        this.intake = reserve(intake);
    }

    public void run(Context context) {

        waitForStatusWithOrTimeout(
                context, Shooter.ShooterStatus.class, s -> s.isCloseToTargetSpeed(), 1);

        intake.setRequest(new Intake.In());

        // Does not stop intake here so driver can stop when button released
    }
}
