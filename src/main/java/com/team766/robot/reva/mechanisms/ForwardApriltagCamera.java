package com.team766.robot.reva.mechanisms;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.RobotSystem;
import com.team766.library.Result;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class ForwardApriltagCamera
        extends RobotSystem<ForwardApriltagCamera.Status, ForwardApriltagCamera.Goal> {

    public record Status(
            boolean isCameraConnected,
            Optional<Integer> tagId,
            Result<Transform3d, AprilTagGeneralCheckedException> speakerTagTransform) {}

    public record Goal() {}

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
    protected Status updateState() {
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

        Result<Transform3d, AprilTagGeneralCheckedException> speakerTagTransform =
                Result.capture(
                        () ->
                                GrayScaleCamera.getBestTargetTransform3d(
                                        camera.getTrackedTargetWithID(tagId)));

        return new Status(
                camera != null && camera.isConnected(),
                tagId != TAG_ID_NOT_CONFIGURED ? Optional.of(tagId) : Optional.empty(),
                speakerTagTransform);
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        // no-op
    }
}
