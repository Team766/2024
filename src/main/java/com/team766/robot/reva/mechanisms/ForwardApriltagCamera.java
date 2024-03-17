package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Optional;

public class ForwardApriltagCamera extends Mechanism {

    private GrayScaleCamera camera;
    private int tagId;

    public ForwardApriltagCamera() throws AprilTagGeneralCheckedException {
        camera = new GrayScaleCamera("Main_Test_Camera_2024");

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = 7;
            } else {
                tagId = 4;
            }
        } else {
            throw new AprilTagGeneralCheckedException("Couldn't find alliance correctly");
        }
    }

    public GrayScaleCamera getCamera() {
        return camera;
    }

    public void run() {
        camera.updateLatestResult();

        double seconds = camera.getLatestTimeStamp();

        try {
            Transform3d toUse =
                    GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));

            SmartDashboard.putNumber("x value SUIIII", toUse.getX());
            SmartDashboard.putNumber("y value SUIIII", toUse.getY());
        } catch (AprilTagGeneralCheckedException e) {
            return;
        }
    }
}
