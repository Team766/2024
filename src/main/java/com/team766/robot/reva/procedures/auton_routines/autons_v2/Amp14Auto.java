package com.team766.robot.reva.procedures.auton_routines.autons_v2;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;


public class Amp14Auto extends PathSequenceAuto {
	public Amp14Auto() {
		super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Amp Subwoofer, Get Amp Wing, End Amp Subwoofer");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Amp Subwoofer, Get Amp Midfield, End Amp Subwoofer");
		addProcedure(new ShootAtSubwoofer());
	}

}
