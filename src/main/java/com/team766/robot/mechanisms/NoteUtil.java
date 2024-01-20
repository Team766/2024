package com.team766.robot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;

public class NoteUtil extends Mechanism {

    public NoteUtil() {}

    public void test() {
        try {
            StaticCameras.noteDetectorCamera.getTagIdOfBestTarget();

            log("yayyayay");

        } catch (AprilTagGeneralCheckedException e) {
            log(e.toString());
        }
    }
}
