package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FivePieceMidStart extends PathSequenceAuto {
    public FivePieceMidStart() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("get-top-note-shoot");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("get-top-far-shoot-middle-start");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Mid-Field-Pick-one-shoot");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Pick1-stage");
		addProcedure(new ShootAtSubwoofer());
    }
}

//this auton shoots the prenote the grabst the close note in the top, then shoots it then it goes to the middle
//note then gets close into subwoofer to shot , then grabs the one infront, then subwoofer again, then grabs the one
//in the stage the subwoofer again