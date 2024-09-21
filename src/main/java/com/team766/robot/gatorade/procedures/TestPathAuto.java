package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TestPathAuto extends PathSequenceAuto {
    private final SwerveDrive drive;

    public TestPathAuto(SwerveDrive drive) {
        super(drive, new Pose2d(2.00, 7.00, new Rotation2d()));
        this.drive = reserve(drive);
    }

    @Override
    protected void runSequence(Context context) {
        runPath(context, "RotationTest");
        drive.setRequest(new SwerveDrive.SetCross());
    }
}
