package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TwoPieceSourceSide extends PathSequenceAuto {
    public TwoPieceSourceSide() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("get-stage-note-shoot");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("get-bottom-mid-second-nopte-shoot");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("From-bottomside-to-mid-close-note-then-shoot");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Grab-ona-amp-side-top");
		addProcedure(new ShootAtSubwoofer());

    }
}