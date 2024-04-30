package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.constants.FollowPointsInputConstants;

public class OPECHelper extends Procedure {

    private static final double DIST = 4;

    private final Drive drive;

    public OPECHelper(Drive drive) {
        super(reservations(drive));
        this.drive = drive;
    }

    public void run(Context context) {
        double startY = drive.getCurrentPosition().getY();
        // robot gyro is offset 90º from how we want, so we reset it to 90º to account for this
        drive.resetGyro();
        // context.runSync(new IntakeRelease());
        drive.controlFieldOriented(0, -FollowPointsInputConstants.SPEED, 0);
        context.waitFor(() -> Math.abs(drive.getCurrentPosition().getY() - startY) > DIST);
        drive.stopDrive();
    }
}
