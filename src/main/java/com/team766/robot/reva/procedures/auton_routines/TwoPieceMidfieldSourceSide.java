package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TwoPieceMidfieldSourceSide extends PathSequenceAuto {
    public TwoPieceMidfieldSourceSide() {
        super(Robot.drive, new Pose2d(0.71, 4.40, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootAtSubwoofer());
        addProcedure(new StartAutoIntake());
        addPath("Bottom Start to Bottom Midfield"); // moves to midfield position
        addPath("Bottom Midfield to Bottom Start"); // moves to subwoofer scoring position
        addProcedure(new ShootAtSubwoofer());
    }
}
