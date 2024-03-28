package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.ScoringPositions;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;

public class ShootTrap extends VisionPIDProcedure {

    int tagId;

    public void run(Context context) {

        while (true) {
            context.yield();
            try {
                tagId = getTagId();
                break;
            } catch (AprilTagGeneralCheckedException e) {
                continue;
            }
        }

        for (int i = 0; i < ScoringPositions.trapScoringPositions.size(); i++) {
            if (ScoringPositions.trapScoringPositions.get(i).tagId == tagId) {
                new DriveToAndScoreAt(ScoringPositions.trapScoringPositions.get(i)).run(context);
                break;
            }
        }
    }

    private int getTagId() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        return toUse.getTagIdOfBestTarget();
    }
}
