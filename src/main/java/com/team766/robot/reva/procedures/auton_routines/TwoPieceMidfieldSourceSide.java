package com.team766.robot.reva.procedures.auton_routines;

import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.ShootAtSubwoofer;
import com.team766.robot.reva.procedures.StartAutoIntake;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class TwoPieceMidfieldSourceSide extends AutoBase {
    public TwoPieceMidfieldSourceSide(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Climber climber,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(drive, shooter, climber, new Pose2d(0.71, 4.40, Rotation2d.fromDegrees(-60)));
        addProcedure(new ShootAtSubwoofer(shoulder, shooter, intake));
        addProcedure(new StartAutoIntake(shoulder, intake));
        addPath("Bottom Start to Bottom Midfield"); // moves to midfield position
        addPath("Bottom Midfield to Bottom Start"); // moves to subwoofer scoring position
        addProcedure(new ShootAtSubwoofer(shoulder, shooter, intake));
    }
}
