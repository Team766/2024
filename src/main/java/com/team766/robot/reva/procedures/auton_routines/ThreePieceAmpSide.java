package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceAmpSide extends AutoBase {
    public ThreePieceAmpSide(
            Drive drive, Shoulder shoulder, Shooter shooter, Intake intake, Climber climber) {
        super(drive, shooter, climber, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer(shoulder, shooter, intake));
        addProcedure(new StartAutoIntake(shoulder, intake));
        addPath("Amp Side Start to Top Piece");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake));
        addProcedure(new StartAutoIntake(shoulder, intake));
        addPath("Top Piece to Middle Piece");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake));
    }
}
