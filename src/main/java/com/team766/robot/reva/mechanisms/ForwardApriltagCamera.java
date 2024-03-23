package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Optional;

public class ForwardApriltagCamera extends Mechanism {

    private GrayScaleCamera camera;
    private int tagId = -1;

    public ForwardApriltagCamera() throws AprilTagGeneralCheckedException {
        camera = new GrayScaleCamera("Main_Test_Camera_2024");

        if (camera.isConnected()) {
            Robot.lights.signalCameraConnected();
        } else {
            Robot.lights.signalCameraNotConnected();
        }
    }

    public GrayScaleCamera getCamera() {
        return camera;
    }

    public void run() {
        if (tagId == -1) {
            Optional<Alliance> alliance = DriverStation.getAlliance();

            if (alliance.isPresent()) {
                if (alliance.get().equals(Alliance.Blue)) {
                    tagId = 7;
                } else {
                    tagId = 4;
                }
                Robot.lights.signalCameraConnected();
            } else {
                LoggerExceptionUtils.logException(
                        new AprilTagGeneralCheckedException("Couldn't find alliance correctly"));
            }
        }

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
