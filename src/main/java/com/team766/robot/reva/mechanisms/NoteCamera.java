package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.ColorCamera;
import com.team766.framework3.SensorMechanism;
import com.team766.framework3.Status;
import java.util.Optional;

public class NoteCamera extends SensorMechanism<NoteCamera.NoteCameraStatus> {
    public record NoteCameraStatus(Optional<Double> yawOfRing, Optional<Double> pitchOfRing)
            implements Status {}

    private ColorCamera camera;

    public NoteCamera() {
        camera = new ColorCamera("Note Detection Camera");
    }

    @Override
    protected NoteCameraStatus run() {
        Optional<Double> yawOfRing;
        try {
            yawOfRing = Optional.of(camera.getYawOfRing());
        } catch (AprilTagGeneralCheckedException ex) {
            yawOfRing = Optional.empty();
        }
        Optional<Double> pitchOfRing;
        try {
            pitchOfRing = Optional.of(camera.getPitchOfRing());
        } catch (AprilTagGeneralCheckedException ex) {
            pitchOfRing = Optional.empty();
        }
        return new NoteCameraStatus(yawOfRing, pitchOfRing);
    }
}
