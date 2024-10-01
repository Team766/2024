package com.team766.robot.reva.procedures;

import static com.team766.framework.Conditions.checkForStatusWith;
import static com.team766.framework.StatusBus.getStatus;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.SwerveDrive;
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

    private final SwerveDrive drive;
    private final Intake intake;

    public PickupNote(SwerveDrive drive, Intake intake) {
        this.drive = reserve(drive);
        this.intake = reserve(intake);
    }

    // button needs to be held down
    public void run(Context context) {
        yawPID.setSetpoint(0.00);

        // Run intake the whole time
        intake.setRequest(new Intake.SetPowerForSensorDistance());

        while (!checkForStatusWith(Intake.IntakeStatus.class, s -> s.hasNoteInIntake())) {
            Optional<Double> yawInDegrees =
                    getStatus(NoteCamera.NoteCameraStatus.class).flatMap(s -> s.yawOfRing());
            if (!yawInDegrees.isPresent()) {
                break;
            }
            yawPID.calculate(yawInDegrees.orElseThrow());
            double power = yawPID.getOutput();

            if (power > 0 && yawInDegrees.orElseThrow() > 0) {
                power *= -1;
            }

            log("power: " + power);

            if (Math.abs(power) > 0.045) {
                // x needs inverted if camera is on front (found out through tests)
                drive.setRequest(new SwerveDrive.RobotOrientedVelocity(power, 0, 0));
            } else {
                drive.setRequest(new SwerveDrive.RobotOrientedVelocity(0, -0.3, 0));
            }

            // double pitchInDegrees = Robot.noteDetectorCamera.getCamera().getPitchOfRing();
            // return status.RING_IN_VIEW;

            context.yield();
        }
        intake.setRequest(new Intake.Stop());
    }
}
