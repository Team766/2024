package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToHuman extends MoveWristvator {

    public ExtendWristvatorToHuman(GamePieceType gamePieceType) {
        super(
                Shoulder.Position.RAISED,
                gamePieceType == GamePieceType.CONE
                        ? Elevator.Position.HUMAN_CONES
                        : Elevator.Position.HUMAN_CUBES,
                gamePieceType == GamePieceType.CONE
                        ? Wrist.Position.HUMAN_CONES
                        : Wrist.Position.HUMAN_CUBES);
    }
}
