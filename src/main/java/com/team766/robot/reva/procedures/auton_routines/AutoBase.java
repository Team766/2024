package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Pose2d;

public abstract class AutoBase extends PathSequenceAuto {

    private final Shooter shooter;
    private final Superstructure superstructure;

    public AutoBase(
            SwerveDrive drive,
            Superstructure superstructure,
            Shooter shooter,
            Pose2d initialPosition) {
        super(drive, initialPosition);
        this.shooter = reserve(shooter);
        this.superstructure = reserve(superstructure);
    }

    protected abstract void runAuto(Context context);

    @Override
    protected final void runSequence(Context context) {
        superstructure.setRequest(Climber.MoveToPosition.BOTTOM);
        try {
            runAuto(context);
        } finally {
            shooter.setRequest(new Shooter.Stop());
        }
    }
}
