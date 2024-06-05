package com.team766.robot.gatorade.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Superstructure;

@CollectReservations
public class ScoreHigh extends MagicProcedure<ScoreHigh_Reservations> {
    private final GamePieceType type;

    @Reserve Superstructure superstructure;

    @Reserve Intake intake;

    public ScoreHigh(GamePieceType type) {
        this.type = type;
    }

    public void run(Context context) {
        superstructure.setGoal(Superstructure.MoveToPosition.EXTENDED_TO_HIGH);
        context.waitFor(() -> superstructure
                .getStatus()
                .isNearTo(Superstructure.MoveToPosition.EXTENDED_TO_HIGH));
        intake.setGoal(new Intake.Status(type, Intake.MotorState.OUT));
        context.waitForSeconds(1);
        intake.setGoal(new Intake.Status(type, Intake.MotorState.STOP));
    }
}
