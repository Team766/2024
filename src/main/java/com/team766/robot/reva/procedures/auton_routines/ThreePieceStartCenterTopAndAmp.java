package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class ThreePieceStartCenterTopAndAmp
        extends AutoBase<ThreePieceStartCenterTopAndAmp_Reservations> {
    @Reserve Drive drive;

    @Reserve Superstructure superstructure;

    @Reserve Shooter shooter;

    @Reserve Intake intake;

    public ThreePieceStartCenterTopAndAmp() {
        super(new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootAtSubwoofer());
        context.runSync(new StartAutoIntake());
        runPath(context, "Middle Start to Middle Piece");
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "Middle Piece to Top Piece");
        context.runSync(new ShootNow());
    }
}
