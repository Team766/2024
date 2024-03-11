package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FourPieceAmpSide extends PathSequenceAuto {
    public FourPieceAmpSide() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
    }

    @Override
    protected void runSequence(Context context) {
        context.runSync(new ShootAtSubwoofer());
        context.runSync(new StartAutoIntake());
        runPath(context, "Amp Side Start to Top Piece");
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "Fast Top Piece to Middle Piece");
        context.runSync(new ShootNow());
        context.runSync(new StartAutoIntake());
        runPath(context, "Middle Piece to Bottom Piece");
        context.runSync(new ShootNow());
    }
}
