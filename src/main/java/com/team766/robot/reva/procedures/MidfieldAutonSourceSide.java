package com.team766.robot.reva.procedures;

import com.team766.framework.annotations.CollectReservations;
import com.team766.robot.reva.procedures.auton_routines.AutoBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class MidfieldAutonSourceSide extends AutoBase<MidfieldAutonSourceSide_Reservations> {
    public MidfieldAutonSourceSide() {
        super(new Pose2d(0.71, 4.39, Rotation2d.fromDegrees(-60)));
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "MidfieldSource 1");
        runPath(context, "MidfieldSource 2");
        context.runSync(new ShootNow());
    }
}
