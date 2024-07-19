package com.team766.robot.reva.procedures.auton_routines.autons_v2;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class JustShootSource extends PathSequenceAuto {
    public JustShootSource() {
        super(Robot.drive, new Pose2d(0.71, 4.40, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootAtSubwoofer());
    }
}
