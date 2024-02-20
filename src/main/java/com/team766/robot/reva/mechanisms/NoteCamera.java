package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.ColorCamera;
import com.team766.framework.Mechanism;

public class NoteCamera extends Mechanism {

    private ColorCamera camera;

    public NoteCamera() {
        camera = new ColorCamera("Note Detection Camera");
    }

    public ColorCamera getCamera() {
        return camera;
    }
}
