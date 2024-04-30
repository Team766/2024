package com.team766.robot.gatorade.procedures;

import com.team766.framework.InstantProcedure;
import com.team766.robot.gatorade.mechanisms.Intake;

public class IntakeOut extends InstantProcedure {
    private final Intake intake;

    public IntakeOut(Intake intake) {
        super(reservations(intake));
        this.intake = intake;
    }

    public void run() {
        intake.out();
    }
}
