package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceBalance extends Procedure {
    private final GamePieceType type;

    public OnePieceBalance(GamePieceType type) {
        this.type = type;
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        // TODO: how do we reset the gyro at 180 degrees?
        // Robot.drive.resetGyro180();
        Robot.drive.resetGyro();

        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            switch (alliance.get()) {
                case Blue:
                    Robot.drive.setCurrentPosition(new Pose2d(2.7, 2, new Rotation2d()));
                    break;
                case Red:
                    Robot.drive.setCurrentPosition(new Pose2d(2.7, 14.5, new Rotation2d()));
                    break;
                default:
                    log("invalid alliance");
                    return;
            }
        } else {
            log("invalid alliance");
            return;
        }
        new ScoreHigh(type).run(context);
        new GyroBalance(alliance.get()).run(context);
    }
}
