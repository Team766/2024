package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ScoreHigh extends Procedure {
    private final GamePieceType type;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;

    public ScoreHigh(
            GamePieceType type, Shoulder shoulder, Elevator elevator, Wrist wrist, Intake intake) {
        super(reservations(shoulder, elevator, wrist, intake));
        this.type = type;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;
    }

    public void run(Context context) {
        context.runSync(new ExtendWristvatorToHigh(shoulder, elevator, wrist));
        intake.setGoal(new Intake.State(type, Intake.MotorState.OUT));
        context.waitForSeconds(1);
        intake.setGoal(new Intake.State(type, Intake.MotorState.STOP));
    }
}
