package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceMidStart extends PathSequenceAuto {
    public ThreePieceMidStart() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Pick1-stage");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
        addPath("Mid-Field-Pick-one-shoot");
		addProcedure(new ShootAtSubwoofer());
    }
}

//this auton gets the note in the stage and shots Subwoofer and trhen the middle close note and shoots Subwoofer