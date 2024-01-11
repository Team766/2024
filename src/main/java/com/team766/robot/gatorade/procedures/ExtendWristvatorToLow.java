package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.constants.*;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class ExtendWristvatorToLow extends MoveWristvator {

    public ExtendWristvatorToLow() {
        super(Shoulder.Position.FLOOR, Elevator.Position.LOW, Wrist.Position.LEVEL);
    }
}
