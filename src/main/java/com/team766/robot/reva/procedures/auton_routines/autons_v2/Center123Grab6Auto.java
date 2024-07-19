package com.team766.robot.reva.procedures.auton_routines.autons_v2;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class Center123Grab6Auto extends PathSequenceAuto {
    public Center123Grab6Auto() {
        super(Robot.drive, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
        addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Center Subwoofer, Get Center Wing, End Center Subwoofer");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Center Subwoofer, Get Amp Wing, End Center Subwoofer");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Center Subwoofer, Get Source Wing, End Center Subwoofer");
		addProcedure(new ShootAtSubwoofer());

		addProcedure(new StartAutoIntake());
		addPath("Start Center Subwoofer, Get Center Midfield, End Center Midfield");
    }
}
