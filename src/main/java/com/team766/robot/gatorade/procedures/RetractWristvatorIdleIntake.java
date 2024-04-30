package com.team766.robot.gatorade.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.mechanisms.Elevator;
import com.team766.robot.gatorade.mechanisms.Intake;
import com.team766.robot.gatorade.mechanisms.Shoulder;
import com.team766.robot.gatorade.mechanisms.Wrist;

public class RetractWristvatorIdleIntake extends Procedure {
    private final Shoulder shoulder;
    private final Elevator elevator;
    private final Wrist wrist;
    private final Intake intake;

    public RetractWristvatorIdleIntake(
            Shoulder shoulder, Elevator elevator, Wrist wrist, Intake intake) {
        super(reservations(shoulder, elevator, wrist, intake));
        this.shoulder = shoulder;
        this.elevator = elevator;
        this.wrist = wrist;
        this.intake = intake;
    }

    public void run(Context context) {
        context.runSync(new RetractWristvator(shoulder, elevator, wrist));
        context.runSync(new IntakeIdle(intake));
    }
}
