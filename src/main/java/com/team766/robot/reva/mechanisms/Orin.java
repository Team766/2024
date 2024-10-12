package com.team766.robot.reva.mechanisms;

import java.util.ArrayList;
import com.team766.framework.Mechanism;
import com.team766.orin.GetApriltagPoseData;
import com.team766.robot.reva.Robot;
import edu.wpi.first.apriltag.AprilTag;

public class Orin extends Mechanism {
    public Orin(){

    }

    public void run(){
        ArrayList<AprilTag> tags = GetApriltagPoseData.getAllTags();

        if(tags.size() > 0){
            Robot.lights.signalHasTag();
        }
    }
    
}
