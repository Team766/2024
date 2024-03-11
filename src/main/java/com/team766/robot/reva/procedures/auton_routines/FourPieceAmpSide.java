package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
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

public class FourPieceAmpSide extends AutoBase {
    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;

    public FourPieceAmpSide(
            Drive drive, Shoulder shoulder, Shooter shooter, Intake intake, Climber climber) {
        super(
                reservations(drive, shoulder, shooter, intake),
                drive,
                shooter,
                climber,
                new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        this.drive = drive;
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootAtSubwoofer(shoulder, shooter, intake));
        context.runSync(new StartAutoIntake(shoulder, intake));
        runPath(context, "Amp Side Start to Top Piece");
        context.runSync(new ShootNow(drive, shoulder, shooter, intake));
        context.runSync(new StartAutoIntake(shoulder, intake));
        runPath(context, "Fast Top Piece to Middle Piece");
        context.runSync(new ShootNow(drive, shoulder, shooter, intake));
        context.runSync(new StartAutoIntake(shoulder, intake));
        runPath(context, "Middle Piece to Bottom Piece");
        context.runSync(new ShootNow(drive, shoulder, shooter, intake));
    }
}
