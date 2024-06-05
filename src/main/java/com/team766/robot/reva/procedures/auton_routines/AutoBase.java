package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Pose2d;

@CollectReservations
public abstract class AutoBase<Reservations extends AutoBase_Reservations>
        extends PathSequenceAuto<Reservations> {

    @Reserve Shooter shooter;

    @Reserve Superstructure superstructure;

    public AutoBase(Pose2d initialPosition) {
        super(initialPosition);
    }

    protected abstract void runAuto(Context context);

    @Override
    protected final void runSequence(Context context) {
        superstructure.setGoal(Climber.MoveToPosition.BOTTOM);
        try {
            runAuto(context);
        } finally {
            shooter.setGoal(new Shooter.Stop());
        }
    }
}
