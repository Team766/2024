package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class LoopAuto extends PathSequenceAuto {
    public LoopAuto(SwerveDrive drive) {
        super(drive, new Pose2d(2.00, 7.00, new Rotation2d(0)));
    }

    @Override
    protected void runSequence(Context context) {
        for (int i = 0; i < 5; i++) {
            runPath(context, "Loop Test");
        }
    }
}
