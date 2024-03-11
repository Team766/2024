package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TestPathAuto extends PathSequenceAuto {
    private final Drive drive;

    public TestPathAuto(Drive drive) {
        super(reservations(drive), drive, new Pose2d(2.00, 7.00, new Rotation2d()));
        this.drive = drive;
    }

    @Override
    protected void runSequence(Context context) {
        runPath(context, "RotationTest");
        drive.setGoal(new Drive.SetCross());
    }
}
