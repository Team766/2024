package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToMid extends MoveWristvator {

    public ExtendWristvatorToMid(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.RotateToPosition.RAISED,
                Elevator.MoveToPosition.MID,
                Wrist.RotateToPosition.MID_NODE,
                shoulder,
                elevator,
                wrist);
    }
}
