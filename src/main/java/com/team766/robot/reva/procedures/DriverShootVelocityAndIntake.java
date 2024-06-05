package com.team766.robot.reva.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;

@CollectReservations
public class DriverShootVelocityAndIntake
        extends MagicProcedure<DriverShootVelocityAndIntake_Reservations> {
    @Reserve Intake intake;

    public void run(Context context) {

        context.waitForConditionOrTimeout(
                () -> getStatus(Shooter.Status.class).get().isCloseToTargetSpeed(), 1);

        intake.setGoal(new Intake.In());

        // Does not stop intake here so driver can stop when button released
    }
}
