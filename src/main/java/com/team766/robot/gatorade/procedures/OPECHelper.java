package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.constants.FollowPointsInputConstants;

public class OPECHelper extends Procedure {

    private static final double DIST = 4;

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        // context.takeOwnership(Robot.intake);
        double startY = Robot.drive.getCurrentPosition().getY();
        // robot gyro is offset 90º from how we want, so we reset it to 90º to account for this
        Robot.drive.resetGyro();
        // new IntakeRelease().run(context);
        Robot.drive.controlFieldOriented(
                Math.toRadians(Robot.drive.getHeading()), 0, -FollowPointsInputConstants.SPEED, 0);
        context.waitFor(() -> Math.abs(Robot.drive.getCurrentPosition().getY() - startY) > DIST);
        Robot.drive.stopDrive();
    }
}
