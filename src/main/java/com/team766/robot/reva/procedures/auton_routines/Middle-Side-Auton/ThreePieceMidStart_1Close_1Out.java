package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceMidStart_1close_1out extends PathSequenceAuto {
    public ThreePieceMidStart_1close_1out() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("get-note-shoot");
		addProcedure(new ShootNow());

		addProcedure(new StartAutoIntake());
        addPath("get-note-under-stage-mid-far");
		addProcedure(new ShootNow());
    }
}


//this auton gerts the topm close note then shoots it the goes under stage t grab the middle note in the middle 
//then goes back where it shooted the first note and shoots it
