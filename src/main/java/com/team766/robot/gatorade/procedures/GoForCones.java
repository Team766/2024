package com.team766.robot.gatorade.procedures;

import com.team766.framework.InstantProcedure;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class GoForCones extends InstantProcedure {
    private final Intake intake;

    public GoForCones(Intake intake) {
        super(reservations(intake));
        this.intake = intake;
    }

    @Override
    public void run() {
        intake.setGamePieceType(GamePieceType.CONE);
    }
}
