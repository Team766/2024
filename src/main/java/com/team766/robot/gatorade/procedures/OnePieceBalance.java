package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.gatorade.mechanisms.Arm;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceBalance extends Procedure {
    private final GamePieceType type;
    private final SwerveDrive drive;
    private final Arm arm;
    private final Intake intake;

    public OnePieceBalance(GamePieceType type, SwerveDrive drive, Arm arm, Intake intake) {
        this.type = type;
        this.drive = reserve(drive);
        this.arm = reserve(arm);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        // TODO: how do we reset the gyro at 180 degrees?
        // Robot.drive.resetGyro180();
        drive.resetGyro();

        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            switch (alliance.get()) {
                case Blue:
                    drive.setCurrentPosition(new Pose2d(2.7, 2, new Rotation2d()));
                    break;
                case Red:
                    drive.setCurrentPosition(new Pose2d(2.7, 14.5, new Rotation2d()));
                    break;
                default:
                    log("invalid alliance");
                    return;
            }
        } else {
            log("invalid alliance");
            return;
        }
        context.runSync(new ScoreHigh(type, arm, intake));
        context.runSync(new GyroBalance(alliance.get(), drive, arm));
    }
}
