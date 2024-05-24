package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.auton_routines.AutoBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class MidfieldAutonSourceSide extends AutoBase {
    private final Drive drive;
    private final Superstructure superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public MidfieldAutonSourceSide(
            Drive drive, Superstructure superstructure, Shooter shooter, Intake intake) {
        super(
                reservations(drive, superstructure, shooter, intake),
                drive,
                superstructure,
                shooter,
                new Pose2d(0.71, 4.39, Rotation2d.fromDegrees(-60)));
        this.drive = drive;
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootNow(drive, superstructure, shooter, intake));
        context.runSync(new StartAutoIntake(superstructure, intake));
        runPath(context, "MidfieldSource 1");
        runPath(context, "MidfieldSource 2");
        context.runSync(new ShootNow(drive, superstructure, shooter, intake));
    }
}
