package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Superstructure;

public class ScoreHigh extends Procedure {
    private final GamePieceType type;
    private final Superstructure superstructure;
    private final Intake intake;

    public ScoreHigh(GamePieceType type, Superstructure superstructure, Intake intake) {
        super(reservations(superstructure, intake));
        this.type = type;
        this.superstructure = superstructure;
        this.intake = intake;
    }

    public void run(Context context) {
        superstructure.setGoal(Superstructure.MoveToPosition.EXTENDED_TO_HIGH);
        context.waitFor(
                () ->
                        superstructure
                                .getStatus()
                                .isNearTo(Superstructure.MoveToPosition.EXTENDED_TO_HIGH));
        intake.setGoal(new Intake.Status(type, Intake.MotorState.OUT));
        context.waitForSeconds(1);
        intake.setGoal(new Intake.Status(type, Intake.MotorState.STOP));
    }
}
