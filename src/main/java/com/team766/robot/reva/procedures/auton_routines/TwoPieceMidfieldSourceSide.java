package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TwoPieceMidfieldSourceSide extends PathSequenceAuto {
    public TwoPieceMidfieldSourceSide() {
        super(Robot.drive, new Pose2d(0.71, 4.40, Rotation2d.fromDegrees(-60)));
    }

    @Override
    protected void runSequence(Context context) {
        context.runSync(new ShootAtSubwoofer());
        context.runSync(new StartAutoIntake());
        runPath(context, "Bottom Start to Bottom Midfield"); // moves to midfield position
        runPath(context, "Bottom Midfield to Bottom Start"); // moves to subwoofer scoring position
        context.runSync(new ShootAtSubwoofer());
    }
}
