package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class MidfieldAutonSourceSide extends PathSequenceAuto {
    public MidfieldAutonSourceSide() {
        super(Robot.drive, new Pose2d(0.71, 4.39, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootNow());
        addProcedure(new StartAutoIntake());
        addPath("MidfieldSource 1");
        addPath("MidfieldSource 2");
        addProcedure(new ShootNow());
    }
}
