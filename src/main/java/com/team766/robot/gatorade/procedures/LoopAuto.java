package com.team766.robot.gatorade.procedures;

import com.team766.framework.annotations.CollectReservations;
import com.team766.robot.common.procedures.PathSequenceAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class LoopAuto extends PathSequenceAuto<LoopAuto_Reservations> {
    public LoopAuto() {
        super(new Pose2d(2.00, 7.00, new Rotation2d(0)));
    }

    @Override
    protected void runSequence(Context context) {
        for (int i = 0; i < 5; i++) {
            runPath(context, "Loop Test");
        }
    }
}
