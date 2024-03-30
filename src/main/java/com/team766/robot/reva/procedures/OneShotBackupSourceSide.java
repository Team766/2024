package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class OneShotBackupSourceSide extends PathSequenceAuto {
    public OneShotBackupSourceSide() {
        super(Robot.drive, new Pose2d(0.72, 4.41, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootNow());
        addPath("BackupSource");
    }
}
