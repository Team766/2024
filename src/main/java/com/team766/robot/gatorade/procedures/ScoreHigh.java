package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;

public class ScoreHigh extends Procedure {
    private final GamePieceType type;

    public ScoreHigh(GamePieceType type) {
        this.type = type;
    }

    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        Robot.intake.setGamePieceType(type);
        context.releaseOwnership(Robot.intake);
        context.runSync(new ExtendWristvatorToHigh());
        context.runSync(new IntakeOut());
        context.waitForSeconds(1);
        context.runSync(new IntakeStop());
    }
}
