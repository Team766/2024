package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;

public class NoteUtil extends Mechanism {

    public NoteUtil() {}

    public String toString() {
        try {
            return "Yaw: " + StaticCameras.noteDetectorCamera.getYawOfRing() + " Pitch: " + StaticCameras.noteDetectorCamera.getPitchOfRing();
        } catch (AprilTagGeneralCheckedException e) {
            return "ERROR: " + 
            e.toString();
        }
    }

    public int getStatus(){

        try{
            StaticCameras.noteDetectorCamera.getRing();
        } catch (AprilTagGeneralCheckedException e){
            return 2;
        }

        return 1; 
    }
}
