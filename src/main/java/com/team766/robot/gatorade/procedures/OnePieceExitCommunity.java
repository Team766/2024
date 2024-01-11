package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceExitCommunity extends Procedure {
    private final GamePieceType type;

    public OnePieceExitCommunity(GamePieceType type) {
        this.type = type;
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        // context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.gyro);
        Robot.gyro.resetGyro180();

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            switch (alliance.get()) {
                case Blue:
                    Robot.drive.setCurrentPosition(new PointDir(0.75, 2));
                    break;
                case Red:
                    Robot.drive.setCurrentPosition(new PointDir(0.75, 14.5));
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
        new ScoreHigh(type).run(context);
        new RetractWristvator().run(context);
        new ExitCommunity().run(context);
    }
}
