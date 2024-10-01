package com.team766.robot.reva.procedures;

import static com.team766.framework.Conditions.waitForStatusWith;
import static com.team766.framework.StatusBus.publishStatus;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.framework.Status;
import com.team766.robot.reva.mechanisms.Intake;

public class IntakeUntilIn extends Procedure {
    public record IntakeUntilInStatus(boolean noteInIntake) implements Status {}

    private final Intake intake;

    public IntakeUntilIn(Intake intake) {
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        publishStatus(new IntakeUntilInStatus(false));
        intake.setRequest(new Intake.SetPowerForSensorDistance());
        waitForStatusWith(context, Intake.IntakeStatus.class, s -> s.hasNoteInIntake());
        publishStatus(new IntakeUntilInStatus(true));
        waitForStatusWith(context, Intake.IntakeStatus.class, s -> s.hasNoteInIntake());
    }
}
