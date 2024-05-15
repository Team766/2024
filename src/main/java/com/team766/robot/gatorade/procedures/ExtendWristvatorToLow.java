package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToLow extends MoveWristvator {

    public ExtendWristvatorToLow(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.RotateToPosition.FLOOR,
                Elevator.MoveToPosition.LOW,
                Wrist.RotateToPosition.LEVEL,
                shoulder,
                elevator,
                wrist);
    }
}
