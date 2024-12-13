package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.ShootNow;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class ThreePieceStartCenterTopAndAmp extends AutoBase {
    private final SwerveDrive drive;
    private final ArmAndClimber superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public ThreePieceStartCenterTopAndAmp(
            SwerveDrive drive, ArmAndClimber superstructure, Shooter shooter, Intake intake) {
        super(drive, superstructure, shooter, new Pose2d(1.35, 5.55, Rotation2d.fromDegrees(0)));
        this.drive = reserve(drive);
        this.superstructure = reserve(superstructure);
        this.shooter = reserve(shooter);
        this.intake = reserve(intake);
    }

    @Override
    protected void runAuto(Context context) {
        context.runSync(new ShootAtSubwoofer(superstructure, shooter, intake));
        context.runSync(new StartAutoIntake(superstructure, intake));
        runPath(context, "Middle Start to Middle Piece");
        context.runSync(new ShootNow(drive, superstructure, shooter, intake));
        context.runSync(new StartAutoIntake(superstructure, intake));
        runPath(context, "Middle Piece to Top Piece");
        context.runSync(new ShootNow(drive, superstructure, shooter, intake));
    }
}
