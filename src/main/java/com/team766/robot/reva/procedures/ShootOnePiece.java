package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ShootOnePiece extends PathSequenceAuto {
    public ShootOnePiece() {
        super(Robot.drive, new Pose2d(0.5, 6.5, Rotation2d.fromDegrees(45)));
        addProcedure(new RotateAndShootNow());
    }
}
