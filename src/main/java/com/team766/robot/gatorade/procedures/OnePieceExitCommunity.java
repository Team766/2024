package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceExitCommunity extends Procedure {
    private final GamePieceType type;
    private final Drive drive;
    private final Superstructure superstructure;
    private final Intake intake;

    public OnePieceExitCommunity(
            GamePieceType type, Drive drive, Superstructure superstructure, Intake intake) {
        super(reservations(drive, superstructure, intake));
        this.type = type;
        this.drive = drive;
        this.superstructure = superstructure;
        this.intake = intake;
    }

    public void run(Context context) {
        drive.resetGyro();

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            switch (alliance.get()) {
                case Blue:
                    drive.setCurrentPosition(new Pose2d(0.75, 2, new Rotation2d()));
                    break;
                case Red:
                    drive.setCurrentPosition(new Pose2d(0.75, 14.5, new Rotation2d()));
                    break;
                default:
                    log("invalid alliance");
                    return;
            }
        } else {
            log("invalid alliance");
            return;
        }
        log("exiting");
        context.runSync(new ScoreHigh(type, superstructure, intake));
        superstructure.setGoal(Superstructure.MoveToPosition.RETRACTED);
        context.waitFor(
                () -> superstructure.getStatus().isNearTo(Superstructure.MoveToPosition.RETRACTED));
        context.runSync(new ExitCommunity(drive));
    }
}
