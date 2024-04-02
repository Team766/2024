package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Mechanism;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class ForwardApriltagCamera extends Mechanism {

    private GrayScaleCamera camera;
    private int tagId = -1;

    public ForwardApriltagCamera() throws AprilTagGeneralCheckedException {
        try {
            camera = new GrayScaleCamera("Main_Test_Camera_2024");

            if (camera.isConnected()) {
                // Robot.lights is initialized before this mechanism
                Robot.lights.signalCameraConnected();
            } else {
                Robot.lights.signalCameraNotConnected();
            }
        } catch (Exception e) {
            log("Unable to create GrayScaleCamera");
            LoggerExceptionUtils.logException(e);
            Robot.lights.signalCameraNotConnected();
        }
    }

    public GrayScaleCamera getCamera() {
        return camera;
    }

    public void run() {

        try {
            if (tagId == -1) {
                Optional<Alliance> alliance = DriverStation.getAlliance();

                if (alliance.isPresent()) {
                    if (alliance.get().equals(Alliance.Blue)) {
                        tagId = VisionConstants.MAIN_BLUE_SPEAKER_TAG;
                    } else {
                        tagId = VisionConstants.MAIN_RED_SPEAKER_TAG;
                    }
                    Robot.lights.signalCameraConnected();
                } else {
                    // LoggerExceptionUtils.logException(
                    //         new AprilTagGeneralCheckedException(
                    //                 "Couldn't find alliance correctly"));
                }
            }
            Transform3d toUse =
                    GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));

            // SmartDashboard.putNumber("x value SUIIII", toUse.getX());
            // SmartDashboard.putNumber("y value SUIIII", toUse.getY());
        } catch (Exception e) {
            return;
        }
    }
}
