package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class FourPieceAmpSide extends AutoBase {
    public FourPieceAmpSide(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Climber climber,
            Lights lights,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(drive, shooter, climber, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        addProcedure(new ShootAtSubwoofer(shoulder, shooter, intake, lights));
        addProcedure(new StartAutoIntake(shoulder, intake, lights));
        addPath("Amp Side Start to Top Piece");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake, lights, forwardApriltagCamera));
        addProcedure(new StartAutoIntake(shoulder, intake, lights));
        addPath("Fast Top Piece to Middle Piece");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake, lights, forwardApriltagCamera));
        addProcedure(new StartAutoIntake(shoulder, intake, lights));
        addPath("Middle Piece to Bottom Piece");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake, lights, forwardApriltagCamera));
    }
}
