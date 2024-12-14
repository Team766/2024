package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class PlayoffAuton extends PathSequenceAuto {
    private final ArmAndClimber superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public PlayoffAuton(
            SwerveDrive drive, ArmAndClimber superstructure, Shooter shooter, Intake intake) {
        super(drive, new Pose2d(0.55, 2.13, Rotation2d.fromDegrees(0)));
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    protected void runSequence(Context context) {
        context.waitForSeconds(2.5);
        runPath(context, "Playoff Path 1");
        context.runSync(new ShootAtSubwoofer(superstructure, shooter, intake));
        context.runSync(new StartAutoIntake(superstructure, intake));
        runPath(context, "Bottom Start to Bottom Midfield"); // moves to midfield position
        runPath(context, "Playoff Path 3");
    }
}
