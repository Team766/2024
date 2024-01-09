package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToHigh extends MoveWristvator {

    public ExtendWristvatorToHigh() {
        super(Elevator.Position.HIGH, Wrist.Position.HIGH_NODE);
    }
}
