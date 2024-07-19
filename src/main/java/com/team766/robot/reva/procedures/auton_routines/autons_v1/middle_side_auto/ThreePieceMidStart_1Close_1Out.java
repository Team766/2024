package com.team766.robot.reva.procedures.auton_routines.autons_v1.middle_side_auto;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceMidStart_1Close_1Out extends PathSequenceAuto {
    public ThreePieceMidStart_1Close_1Out() {
        super(Robot.drive, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
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
