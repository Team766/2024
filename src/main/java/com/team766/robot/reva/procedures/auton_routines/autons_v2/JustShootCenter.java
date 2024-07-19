package com.team766.robot.reva.procedures.auton_routines.autons_v2;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class JustShootCenter extends PathSequenceAuto {
    public JustShootCenter() {
        super(Robot.drive, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
        addProcedure(new ShootAtSubwoofer());
    }
}
