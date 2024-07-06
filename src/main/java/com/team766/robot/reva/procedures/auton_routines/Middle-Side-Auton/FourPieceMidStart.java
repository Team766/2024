package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FourPieceMidStart extends PathSequenceAuto {
    public FourPieceMidStart() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Pick1-stage");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Mid-Field-Pick-one-shoot");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Grab-ona-amp-side-top");
		addProcedure(new ShootAtSubwoofer())

		addPath("from-mid-to-run-away");
    }
}

//this is a Four piece it grabs al;l three nothes close and runs away to the m,iddle,
//the firts note is the one close to the stage