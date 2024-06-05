package com.team766.robot.gatorade.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.constants.FollowPointsInputConstants;

@CollectReservations
public class ExitCommunity extends MagicProcedure<ExitCommunity_Reservations> {

    private static final double DIST = 4;

    @Reserve Drive drive;

    public void run(Context context) {
        double startY = drive.getStatus().currentPosition().getY();
        drive.setGoal(new Drive.FieldOrientedVelocity(0, -FollowPointsInputConstants.SPEED, 0));
        context.waitFor(() -> Math.abs(drive.getStatus().currentPosition().getY() - startY) > DIST);
        drive.setGoal(new Drive.StopDrive());
    }
}
