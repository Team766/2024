package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class PlayoffAuton extends PathSequenceAuto {
    public PlayoffAuton() {
        super(Robot.drive, new Pose2d(0.55, 2.13, Rotation2d.fromDegrees(0)));
        addWait(2.5);
        addPath("Playoff Path 1");
        addProcedure(new ShootAtSubwoofer());
        addProcedure(new StartAutoIntake());
        addPath("Bottom Start to Bottom Midfield"); // moves to midfield position
        addPath("Playoff Path 3");
    }
}
