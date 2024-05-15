package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Intake.MotorState;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendToHumanWithIntake extends Procedure {
    private final GamePieceType gamePieceType;
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;

    public ExtendToHumanWithIntake(
            GamePieceType gamePieceType,
            Shoulder shoulder,
            Elevator elevator,
            Wrist wrist,
            Intake intake) {
        super(reservations(shoulder, elevator, wrist, intake));
        this.gamePieceType = gamePieceType;
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;
    }

    public void run(Context context) {
        intake.setGoal(new Intake.State(gamePieceType, MotorState.IN));
        context.runSync(new ExtendWristvatorToHuman(gamePieceType, shoulder, elevator, wrist));
    }
}
