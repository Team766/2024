package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework3.SensorMechanism;
import com.team766.framework3.Status;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class ForwardApriltagCamera
        extends SensorMechanism<ForwardApriltagCamera.ApriltagCameraStatus> {

    public record ApriltagCameraStatus(
            boolean isCameraConnected,
            Optional<Integer> tagId,
            Optional<Transform3d> speakerTagTransform)
            implements Status {}

    private static final int TAG_ID_NOT_CONFIGURED = -1;

    private GrayScaleCamera camera;
    private int tagId = TAG_ID_NOT_CONFIGURED;

    public ForwardApriltagCamera() {
        try {
            camera = new GrayScaleCamera("Main_Test_Camera_2024");
        } catch (Exception e) {
            log("Unable to create GrayScaleCamera");
            LoggerExceptionUtils.logException(e);
        }
    }

    @Override
    protected ApriltagCameraStatus run() {
        if (tagId == TAG_ID_NOT_CONFIGURED) {
            Optional<Alliance> alliance = DriverStation.getAlliance();

            if (alliance.isPresent()) {
                if (alliance.get().equals(Alliance.Blue)) {
                    tagId = VisionConstants.MAIN_BLUE_SPEAKER_TAG;
                } else {
                    tagId = VisionConstants.MAIN_RED_SPEAKER_TAG;
                }
            }
        }

        Optional<Transform3d> speakerTagTransform;
        try {
            speakerTagTransform =
                    Optional.of(
                            GrayScaleCamera.getBestTargetTransform3d(
                                    camera.getTrackedTargetWithID(tagId)));
        } catch (AprilTagGeneralCheckedException ex) {
            speakerTagTransform = Optional.empty();
        }

        return new ApriltagCameraStatus(
                camera != null && camera.isConnected(),
                tagId != TAG_ID_NOT_CONFIGURED ? Optional.of(tagId) : Optional.empty(),
                speakerTagTransform);
    }
}
