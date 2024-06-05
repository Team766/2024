package com.team766.robot.reva.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

@CollectReservations
public final class ShootAtSubwoofer extends MagicProcedure<ShootAtSubwoofer_Reservations> {
    @Reserve Superstructure superstructure;

    public void run(Context context) {
        superstructure.setGoal(Shoulder.RotateToPosition.SHOOT_LOW);
        context.runSync(new ShootVelocityAndIntake());
    }
}
