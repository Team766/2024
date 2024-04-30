package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;

public class IntakeUntilIn extends Procedure {
    private final Intake intake;
    private final Lights lights;

    public IntakeUntilIn(Intake intake, Lights lights) {
        super(reservations(intake));
        this.intake = intake;
        this.lights = lights;
    }

    public void run(Context context) {
        lights.signalNoNoteInIntakeYet();
        while (!intake.hasNoteInIntake()) {
            intake.setIntakePowerForSensorDistance();

            if (intake.isNoteClose()) {
                lights.signalNoteInIntake();
            }
            context.yield();
        }

        // Start an async procedure so that we release the reservation on the Intake.
        context.startAsync(
                new Procedure(NO_RESERVATIONS) {
                    @Override
                    public void run(Context context) {
                        lights.signalNoteInIntake();

                        context.waitForSeconds(2);

                        lights.turnLightsOff();
                    }
                });
    }
}
