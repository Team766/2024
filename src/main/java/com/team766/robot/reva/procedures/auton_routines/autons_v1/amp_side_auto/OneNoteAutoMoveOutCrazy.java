package com.team766.robot.reva.procedures.auton_routines.autons_v1.amp_side_auto;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class OneNoteAutoMoveOutCrazy extends PathSequenceAuto {
    public OneNoteAutoMoveOutCrazy() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());
        addPath("one note auto move out");
    }
}