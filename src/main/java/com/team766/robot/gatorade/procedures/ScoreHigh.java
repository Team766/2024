package com.team766.robot.gatorade.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.gatorade.mechanisms.Arm;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class ScoreHigh extends Procedure {
    private final GamePieceType type;
    private final Arm arm;
    private final Intake intake;

    public ScoreHigh(GamePieceType type, Arm arm, Intake intake) {
        this.type = type;
        this.arm = reserve(arm);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        arm.setRequest(Arm.MoveToPosition.EXTENDED_TO_HIGH);
        context.waitFor(() -> Arm.MoveToPosition.EXTENDED_TO_HIGH.isDone());
        intake.setRequest(new Intake.IntakeState(type, Intake.MotorState.OUT));
        context.waitForSeconds(1);
        intake.setRequest(new Intake.IntakeState(type, Intake.MotorState.STOP));
    }
}
