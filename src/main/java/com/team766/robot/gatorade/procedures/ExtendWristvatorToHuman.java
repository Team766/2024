package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToHuman extends MoveWristvator {

    public ExtendWristvatorToHuman(GamePieceType gamePieceType) {
        super(
                gamePieceType == GamePieceType.CONE
                        ? Elevator.Position.HUMAN_CONES
                        : Elevator.Position.HUMAN_CUBES,
                Wrist.Position.LEVEL);
    }
}
