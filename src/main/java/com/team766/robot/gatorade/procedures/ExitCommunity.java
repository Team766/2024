package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.gatorade.constants.FollowPointsInputConstants;

public class ExitCommunity extends Procedure {

    private static final double DIST = 4;

    private final SwerveDrive drive;

    public ExitCommunity(SwerveDrive drive) {
        this.drive = reserve(drive);
    }

    public void run(Context context) {
        double startY = drive.getMechanismStatus().currentPosition().getY();
        drive.setRequest(
                new SwerveDrive.FieldOrientedVelocity(0, -FollowPointsInputConstants.SPEED, 0));
        context.waitFor(
                () ->
                        Math.abs(drive.getMechanismStatus().currentPosition().getY() - startY)
                                > DIST);
        drive.setRequest(new SwerveDrive.Stop());
    }
}
