package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Amp4NoteAuton1close2farCrazy extends PathSequenceAuto {
    public Amp4NoteAuton1close2farCrazy() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Amp-3-note-auton-1close-2-far");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("get-top-fat-note-shoot-mid");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("from-shoot-mid-to-2nt-top-note-then-shoot-center");
		addProcedure(new ShootNow());

		addPath("from-shotcenter-understage-midnote-far");
    }
}