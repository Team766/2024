package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FourPieceAmpSide extends PathSequenceAuto {
    public FourPieceAmpSide() {
        super(Robot.drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer());
        addProcedure(new StartAutoIntake());
        addPath("Amp Side Start to Top Piece");
        addProcedure(new ShootNow());
        addProcedure(new StartAutoIntake());
        addPath("Fast Top Piece to Middle Piece");
        addProcedure(new ShootNow());
        addProcedure(new StartAutoIntake());
        addPath("Middle Piece to Bottom Piece");
        addProcedure(new ShootNow());
    }
}
