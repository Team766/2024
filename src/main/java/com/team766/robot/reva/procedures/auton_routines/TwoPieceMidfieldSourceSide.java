package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class TwoPieceMidfieldSourceSide extends AutoBase<TwoPieceMidfieldSourceSide_Reservations> {
    @Reserve Superstructure superstructure;

    @Reserve Shooter shooter;

    @Reserve Intake intake;

    public TwoPieceMidfieldSourceSide() {
        super(new Pose2d(0.71, 4.40, Rotation2d.fromDegrees(-60)));
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootAtSubwoofer());
        context.runSync(new StartAutoIntake());
        runPath(context, "Bottom Start to Bottom Midfield"); // moves to midfield position
        runPath(context, "Bottom Midfield to Bottom Start"); // moves to subwoofer scoring position
        context.runSync(new ShootAtSubwoofer());
    }
}
