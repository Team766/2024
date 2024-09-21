package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Superstructure;

public class ScoreHigh extends Procedure {
    private final GamePieceType type;
    private final Superstructure superstructure;
    private final Intake intake;

    public ScoreHigh(GamePieceType type, Superstructure superstructure, Intake intake) {
        this.type = type;
        this.superstructure = reserve(superstructure);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        superstructure.setRequest(Superstructure.MoveToPosition.EXTENDED_TO_HIGH);
        context.waitFor(() -> Superstructure.MoveToPosition.EXTENDED_TO_HIGH.isDone());
        intake.setRequest(new Intake.IntakeState(type, Intake.MotorState.OUT));
        context.waitForSeconds(1);
        intake.setRequest(new Intake.IntakeState(type, Intake.MotorState.STOP));
    }
}
