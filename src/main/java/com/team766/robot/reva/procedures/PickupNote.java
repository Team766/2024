package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.NoteCamera;
import java.util.Optional;

public class PickupNote extends VisionPIDProcedure {

    public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

    private final Drive drive;
    private final Intake intake;

    public PickupNote(Drive drive, Intake intake) {
        super(reservations(drive, intake));
        this.drive = drive;
        this.intake = intake;
    }

    // button needs to be held down
    public void run(Context context) {
        yawPID.setSetpoint(0.00);

        // Run intake the whole time
        intake.setGoal(new Intake.SetPowerForSensorDistance());

        while (!intake.getStatus().hasNoteInIntake()) {
            Optional<Double> yawInDegrees =
                    getStatus(NoteCamera.Status.class).get().yawOfRing();
            if (!yawInDegrees.isPresent()) {
                break;
            }
            yawPID.calculate(yawInDegrees.get());
            double power = yawPID.getOutput();

            if (power > 0 && yawInDegrees.get() > 0) {
                power *= -1;
            }

            log("power: " + power);

            if (Math.abs(power) > 0.045) {
                // x needs inverted if camera is on front (found out through tests)
                drive.setGoal(new Drive.RobotOrientedVelocity(power, 0, 0));
            } else {
                drive.setGoal(new Drive.RobotOrientedVelocity(0, -0.3, 0));
            }

            // double pitchInDegrees = Robot.noteDetectorCamera.getCamera().getPitchOfRing();
            // return status.RING_IN_VIEW;

            context.yield();
        }
        intake.setGoal(new Intake.Stop());
    }
}
