package com.team766.robot.reva.procedures.auton_routines.middle_side_auto;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceMidStart extends PathSequenceAuto {
    public ThreePieceMidStart() {
        super(Robot.drive, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
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