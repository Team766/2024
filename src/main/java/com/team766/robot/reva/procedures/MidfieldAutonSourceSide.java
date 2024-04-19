package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class MidfieldAutonSourceSide extends PathSequenceAuto {
    public MidfieldAutonSourceSide() {
        super(Robot.drive, new Pose2d(0.71, 4.39, Rotation2d.fromDegrees(-60)));
    }

    @Override
    protected void runSequence(Context context) {
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "MidfieldSource 1");
        runPath(context, "MidfieldSource 2");
        context.runSync(new ShootNow());
    }
}
