package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceExitCommunityBalance extends Procedure {
    private final GamePieceType type;
    private final Drive drive;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;

    public OnePieceExitCommunityBalance(
            GamePieceType type,
            Drive drive,
            Shoulder shoulder,
            Elevator elevator,
            Wrist wrist,
            Intake intake) {
        super(reservations(drive, shoulder, elevator, wrist, intake));
        this.type = type;
        this.drive = drive;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;
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
        context.runSync(new ScoreHigh(type, shoulder, elevator, wrist, intake));
        context.runSync(new ExitCommunity(drive));
        log("Transitioning");
        context.runSync(new GyroBalance(alliance.get(), drive, shoulder, elevator, wrist));
    }
}
