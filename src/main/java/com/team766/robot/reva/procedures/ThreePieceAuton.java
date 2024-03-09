package com.team766.robot.reva.procedures;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceAuton extends PathSequenceAuto {
	public ThreePieceAuton() {
		super(Robot.drive, new Pose2d(2.00, 6.75, new Rotation2d(33)));
		// add(vision shoot);
		// add(shoulder down and intake w/ prox sensor);
		add("3 Piece 1");
		add("3 Piece 2");
		// add(vision shoot);
		// add(shoulder down and intake w/ prox sensor);
		add("3 Piece 3");
		// add(vision shoot);
	}
}
