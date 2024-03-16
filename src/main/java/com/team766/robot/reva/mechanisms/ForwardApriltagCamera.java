package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ForwardApriltagCamera extends Mechanism {

    private GrayScaleCamera camera;

    public ForwardApriltagCamera() {
        camera = new GrayScaleCamera("Main_Test_Camera_2024");
    }

    public GrayScaleCamera getCamera() {
        return camera;
    }

    public void run() {
        try {
            Transform3d toUse =
                    GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(7));

            SmartDashboard.putNumber("x value SUIIII", toUse.getX());
            SmartDashboard.putNumber("y value SUIIII", toUse.getY());
        } catch (AprilTagGeneralCheckedException e) {
            return;
        }
    }
}
