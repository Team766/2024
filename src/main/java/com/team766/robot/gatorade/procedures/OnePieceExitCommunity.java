package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.PointDir;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class OnePieceExitCommunity extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        // context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.gyro);
        Robot.gyro.resetGyro();

        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            switch (alliance.get()) {
                case Blue:
                    Robot.drive.setCurrentPosition(new PointDir(2, 0.75));
                    break;
                case Red:
                    Robot.drive.setCurrentPosition(new PointDir(14.5, 0.75));
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
        new OPECHelper().run(context);
    }
}
