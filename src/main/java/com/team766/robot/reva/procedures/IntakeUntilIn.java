package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;

public class IntakeUntilIn extends Procedure {
    public record Status(boolean noteInIntake) {}

    private final Intake intake;

    public IntakeUntilIn(Intake intake) {
        super(reservations(intake));
        this.intake = intake;
    }

    public void run(Context context) {
        updateStatus(new Status(false));
        intake.setGoal(new Intake.SetPowerForSensorDistance());
        context.waitFor(() -> intake.getStatus().hasNoteInIntake());
        updateStatus(new Status(true));
        context.waitFor(() -> intake.getStatus().hasNoteInIntake());
    }
}
