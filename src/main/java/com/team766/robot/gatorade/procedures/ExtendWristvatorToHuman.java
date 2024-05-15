package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake.GamePieceType;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToHuman extends MoveWristvator {

    public ExtendWristvatorToHuman(
            GamePieceType gamePieceType, Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.RotateToPosition.RAISED,
                gamePieceType == GamePieceType.CONE
                        ? Elevator.MoveToPosition.HUMAN_CONES
                        : Elevator.MoveToPosition.HUMAN_CUBES,
                gamePieceType == GamePieceType.CONE
                        ? Wrist.RotateToPosition.HUMAN_CONES
                        : Wrist.RotateToPosition.HUMAN_CUBES,
                shoulder,
                elevator,
                wrist);
    }
}
