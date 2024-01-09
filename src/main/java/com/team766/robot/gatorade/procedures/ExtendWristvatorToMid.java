package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToMid extends MoveWristvator {

    public ExtendWristvatorToMid() {
        super(Elevator.Position.MID, Wrist.Position.MID_NODE);
    }
}
