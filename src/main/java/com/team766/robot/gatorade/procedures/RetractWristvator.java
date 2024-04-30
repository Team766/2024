package com.team766.robot.gatorade.procedures;

import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class RetractWristvator extends MoveWristvator {

    public RetractWristvator(Shoulder shoulder, Elevator elevator, Wrist wrist) {
        super(
                Shoulder.Position.BOTTOM,
                Elevator.Position.RETRACTED,
                Wrist.Position.RETRACTED,
                shoulder,
                elevator,
                wrist);
    }
}
