package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FourPieceAmpSide extends PathSequenceAuto {
	public FourPieceAmpSide() {
		super(Robot.drive, new Pose2d(0.75, 6.68, Rotation2d.fromDegrees(60)));
        addProcedure(new RotateAndShootNow());
        addProcedure(new AutoIntake());
        addPath("3 Piece 1");
        addProcedure(new RotateAndShootNow());
        addProcedure(new AutoIntake());
        addPath("Alternate 3 Piece 2");
        addProcedure(new RotateAndShootNow());
		addProcedure(new AutoIntake());
		addPath("4 Piece 3");
		addProcedure(new RotateAndShootNow());
	}
}
