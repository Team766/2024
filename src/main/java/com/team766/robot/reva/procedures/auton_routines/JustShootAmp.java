package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework3.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class JustShootAmp extends PathSequenceAuto {

    private final ArmAndClimber superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public JustShootAmp(
            SwerveDrive drive, ArmAndClimber superstructure, Shooter shooter, Intake intake) {
        super(drive, new Pose2d(0.71, 6.72, Rotation2d.fromDegrees(60)));
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
    }

    @Override
    protected void runSequence(Context context) {
        context.runSync(new ShootAtSubwoofer(superstructure, shooter, intake));
    }
}
