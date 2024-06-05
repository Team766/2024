package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class AutoBase extends PathSequenceAuto {

    private final Shooter shooter;
    private final Superstructure superstructure;

    public AutoBase(
            Collection<Subsystem> reservations,
            Drive drive,
            Superstructure superstructure,
            Shooter shooter,
            Pose2d initialPosition) {
        super(reservations, drive, initialPosition);
        addReservations(superstructure, shooter);
        this.shooter = shooter;
        this.superstructure = superstructure;
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
