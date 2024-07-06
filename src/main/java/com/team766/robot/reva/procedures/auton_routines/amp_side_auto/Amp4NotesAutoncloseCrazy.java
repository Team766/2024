package com.team766.robot.reva.procedures.auton_routines.amp_side_auto;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Amp4NotesAutoncloseCrazy extends PathSequenceAuto {
    public Amp4NotesAutoncloseCrazy() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("amp-3-notes-auton");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("grab-midnote-close-move-witheline-inforntoflownote");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("get-low-close-note-move-a-little-shoot");
		addProcedure(new ShootNow());

		addPath("afther-shoot-move-front-far");


    }
}