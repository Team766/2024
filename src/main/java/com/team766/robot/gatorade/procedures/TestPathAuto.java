package com.team766.robot.gatorade.procedures;

import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class TestPathAuto extends PathSequenceAuto<TestPathAuto_Reservations> {
    @Reserve Drive drive;

    public TestPathAuto() {
        super(new Pose2d(2.00, 7.00, new Rotation2d()));
    }

    @Override
    protected void runSequence(Context context) {
        runPath(context, "RotationTest");
        drive.setGoal(new Drive.SetCross());
    }
}
