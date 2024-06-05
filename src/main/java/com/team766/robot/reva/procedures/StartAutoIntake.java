package com.team766.robot.reva.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

@CollectReservations
public class StartAutoIntake extends MagicProcedure<StartAutoIntake_Reservations> {
    @Reserve Superstructure superstructure;

    @Reserve Intake intake;

    public void run(Context context) {
        final var armTarget = Shoulder.RotateToPosition.BOTTOM;
        superstructure.setGoal(armTarget);
        context.waitForConditionOrTimeout(
                () -> getStatus(Shoulder.Status.class).get().isNearTo(armTarget), 1.5);
        intake.setGoal(new Intake.SetPowerForSensorDistance());
    }
}
