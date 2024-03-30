package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceAutonAmpSide extends PathSequenceAuto {
    public ThreePieceAutonAmpSide() {
        super(Robot.drive, new Pose2d(0.75, 6.68, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());
        addProcedure(new AutoIntake());
        addPath("3 Piece 1");
        addProcedure(new ShootNow());
        addProcedure(new AutoIntake());
        addPath("Alternate 3 Piece 2");
        addProcedure(new ShootNow());
    }
}
