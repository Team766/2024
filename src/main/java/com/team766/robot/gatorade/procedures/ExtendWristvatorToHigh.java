package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToHigh extends MoveWristvator {

    public ExtendWristvatorToHigh(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.Position.RAISED,
                Elevator.Position.HIGH,
                Wrist.Position.HIGH_NODE,
                shoulder,
                elevator,
                wrist);
    }
}
