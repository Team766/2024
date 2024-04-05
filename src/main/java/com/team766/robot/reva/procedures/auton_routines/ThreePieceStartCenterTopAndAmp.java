package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceStartCenterTopAndAmp extends PathSequenceAuto {
    public ThreePieceStartCenterTopAndAmp() {
        super(Robot.drive, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
        addProcedure(new ShootAtSubwoofer());
        addProcedure(new StartAutoIntake());
        addPath("Middle Start to Middle Piece");
        addProcedure(new ShootNow());
        addProcedure(new StartAutoIntake());
        addPath("Middle Piece to Top Piece");
        addProcedure(new ShootNow());
    }
}
