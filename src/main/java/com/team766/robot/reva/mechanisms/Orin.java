package com.team766.robot.reva.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.orin.GetApriltagPoseData;
import com.team766.orin.NoTagFoundError;
import com.team766.robot.reva.Robot;
import edu.wpi.first.apriltag.AprilTag;
import java.util.ArrayList;

public class Orin extends Mechanism {
    public Orin() {}

    public AprilTag getTagById(int id) throws NoTagFoundError {
        ArrayList<AprilTag> tags = GetApriltagPoseData.getAllTags();

        for (AprilTag tag : tags) {
            if (tag.ID == id) return tag;
        }

        throw new NoTagFoundError(id);
    }

    public void run() {
        ArrayList<AprilTag> tags = GetApriltagPoseData.getAllTags();

        if (tags.size() > 0) {
            Robot.lights.signalHasTag();
        }
    }
}
