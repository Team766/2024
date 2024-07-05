package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Amp3NotesAutoncloseCrazy extends PathSequenceAuto {
    public Amp3NotesAutoncloseCrazy() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("amp-3-notes-auton");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("Move-mid-note-close-shoot");
		addProcedure(new ShootNow());

		addPath("from-shotcenter-understage-midnote-far");
    }
}