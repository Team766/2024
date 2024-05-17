package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.constants.FollowPointsInputConstants;

public class ExitCommunity extends Procedure {

    private static final double DIST = 4;

    private final Drive drive;

    public ExitCommunity(Drive drive) {
        super(reservations(drive));
        this.drive = drive;
    }

    public void run(Context context) {
        double startY = drive.getStatus().currentPosition().getY();
        drive.setGoal(new Drive.FieldOrientedVelocity(0, -FollowPointsInputConstants.SPEED, 0));
        context.waitFor(() -> Math.abs(drive.getStatus().currentPosition().getY() - startY) > DIST);
        drive.setGoal(new Drive.StopDrive());
    }
}
