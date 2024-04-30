package com.team766.robot.reva.procedures;

import com.team766.framework.InstantProcedure;
import com.team766.robot.reva.mechanisms.Intake;

public class IntakeStop extends InstantProcedure {
    private final Intake intake;

    public IntakeStop(Intake intake) {
        super(reservations(intake));
        this.intake = intake;
    }

    public void run() {
        intake.stop();
    }
}
