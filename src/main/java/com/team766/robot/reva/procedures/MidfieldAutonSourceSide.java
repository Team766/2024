package com.team766.robot.reva.procedures;

import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.auton_routines.AutoBase;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class MidfieldAutonSourceSide extends AutoBase {
    public MidfieldAutonSourceSide(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Climber climber,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(drive, shooter, climber, new Pose2d(0.71, 4.39, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootNow(drive, shoulder, shooter, intake, forwardApriltagCamera));
        addProcedure(new StartAutoIntake(shoulder, intake));
        addPath("MidfieldSource 1");
        addPath("MidfieldSource 2");
        addProcedure(new ShootNow(drive, shoulder, shooter, intake, forwardApriltagCamera));
    }
}
