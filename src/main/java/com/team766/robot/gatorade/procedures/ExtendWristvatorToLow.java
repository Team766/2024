package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToLow extends MoveWristvator {

    public ExtendWristvatorToLow(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.Position.FLOOR,
                Elevator.Position.LOW,
                Wrist.Position.LEVEL,
                shoulder,
                elevator,
                wrist);
    }
}
