package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.annotations.CollectReservations;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class ThreePieceAmpSide extends AutoBase<ThreePieceAmpSide_Reservations> {
    public ThreePieceAmpSide() {
        super(new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootAtSubwoofer());
        context.runSync(new StartAutoIntake());
        runPath(context, "Amp Side Start to Top Piece");
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "Top Piece to Middle Piece");
        context.runSync(new ShootNow());
    }
}
