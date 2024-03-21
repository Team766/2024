package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceAuton extends PathSequenceAuto {
    public ThreePieceAuton() {
        super(Robot.drive, new Pose2d(2.00, 6.75, Rotation2d.fromDegrees(33)));
        addProcedure(new MoveClimbersToBottom());
        addProcedure(new RotateAndShootNow());
        addProcedure(new AutoIntake());
        addPath("3 Piece 1");
        addPath("3 Piece 2");
        addProcedure(new RotateAndShootNow());
        addProcedure(new AutoIntake());
        addPath("3 Piece 3");
        addProcedure(new RotateAndShootNow());
    }
}
