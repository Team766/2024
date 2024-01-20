package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;

public class NoteUtil extends Mechanism {

    public NoteUtil() {}

    public void test() {
        try {
            

            
            
            log("Yaw: " + StaticCameras.noteDetectorCamera.getYawOfRing());
            log("Pitch: " + StaticCameras.noteDetectorCamera.getPitchOfRing());

        } catch (AprilTagGeneralCheckedException e) {
            log(e.toString());
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
