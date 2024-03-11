package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class ExtendToHumanWithIntake extends Procedure {
    private final GamePieceType gamePieceType;

    public ExtendToHumanWithIntake(GamePieceType gamePieceType) {
        this.gamePieceType = gamePieceType;
    }

    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.wrist);
        context.takeOwnership(Robot.elevator);

        context.runSync(new IntakeIn());
        context.runSync(new ExtendWristvatorToHuman(gamePieceType));
    }
}
