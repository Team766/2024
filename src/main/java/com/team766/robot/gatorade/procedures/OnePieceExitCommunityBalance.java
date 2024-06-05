package com.team766.robot.gatorade.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

@CollectReservations
public class OnePieceExitCommunityBalance
        extends MagicProcedure<OnePieceExitCommunityBalance_Reservations> {
    private final GamePieceType type;

    @Reserve Drive drive;

    @Reserve Superstructure superstructure;

    @Reserve Intake intake;

    public OnePieceExitCommunityBalance(GamePieceType type) {
        this.type = type;
    }

    public void run(Context context) {
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
        log("exiting");
        context.runSync(new ScoreHigh(type));
        context.runSync(new ExitCommunity());
        log("Transitioning");
        context.runSync(new GyroBalance(alliance.get()));
    }
}
