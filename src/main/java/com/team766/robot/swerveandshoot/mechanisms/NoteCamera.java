package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.ColorCamera;
import com.team766.framework.Subsystem;
import java.util.Optional;

public class NoteCamera extends Subsystem<NoteCamera.Status, NoteCamera.Goal> {
    public record Status(Optional<Double> yawOfRing, Optional<Double> pitchOfRing) {}

    public record Goal() {}

    private ColorCamera camera;

    public NoteCamera() {
        camera = new ColorCamera("Note Detection Camera");
    }

    @Override
    protected Status updateState() {
        return new Status(camera.getYawOfRing(), camera.getPitchOfRing());
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        // no-op
    }
}
