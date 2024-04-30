package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.NoteCamera;

public class PickupNote extends VisionPIDProcedure {

    public enum status {
        RING_IN_VIEW,
        NO_RING_IN_VIEW,
        RING_IN_INTAKE
    }

    private final Drive drive;
    private final Intake intake;
    private final NoteCamera noteCamera;

    public PickupNote(Drive drive, Intake intake, NoteCamera noteCamera) {
        super(reservations(drive, intake));
        this.drive = drive;
        this.intake = intake;
        this.noteCamera = noteCamera;
    }

    // button needs to be held down
    public void run(Context context) {
        yawPID.setSetpoint(0.00);

        try {
            while (!intake.hasNoteInIntake()) {

                double yawInDegrees = noteCamera.getCamera().getYawOfRing();
                yawPID.calculate(yawInDegrees);
                double power = yawPID.getOutput();

                if (power > 0 && yawInDegrees > 0) {
                    power *= -1;
                }

                log("power: " + power);

                if (Math.abs(power) > 0.045) {
                    // x needs inverted if camera is on front (found out through tests)
                    drive.controlRobotOriented(power, 0, 0);
                } else {
                    // Run intake the whole time
                    intake.runIntake();
                    drive.controlRobotOriented(0, -0.3, 0);
                }

                // double pitchInDegrees = Robot.noteDetectorCamera.getCamera().getPitchOfRing();
                // return status.RING_IN_VIEW;

                context.yield();
            }
            intake.stop();

        } catch (AprilTagGeneralCheckedException e) {
            // return status.NO_RING_IN_VIEW;
        }
    }
}
