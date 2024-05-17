package com.team766.robot.reva.VisionUtil;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.SubsystemStatus;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.VisionConstants;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class VisionSpeakerHelper {

    double angle;
    SubsystemStatus<ForwardApriltagCamera.Status> camera;
    Drive drive;
    Translation2d absTargetPos;
    Translation2d relativeTranslation2d;

    // TODO: make this static
    public VisionSpeakerHelper(
            Drive drive, SubsystemStatus<ForwardApriltagCamera.Status> forwardApriltagCamera) {
        camera = forwardApriltagCamera;
        this.drive = drive;
    }

    private void updateAlliance() {
        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                absTargetPos = VisionConstants.MAIN_BLUE_SPEAKER_TAG_POS;
            } else if (alliance.get().equals(Alliance.Red)) {
                absTargetPos = VisionConstants.MAIN_RED_SPEAKER_TAG_POS;
            }
        }
    }

    // TODO: reformat the code to be more efficient
    /**
     * Updates current target position based on odometry robot position and vision
     * @return whether or not it was successfully reset or not, depending on if it sees the tag
     */
    private boolean updateTarget() {
        try {

            // re-calculates the absolute position of the target according to odometry
            // Rotates the direction of the relative translation to correct for robot orientation:
            // Shooter camera is on back of the robot
            // Sticks around even when there is no new valid relativeTarget

            Transform3d transform3d = camera.getStatus().speakerTagTransform().get();
            Translation2d relativeTarget =
                    new Translation2d(transform3d.getX(), transform3d.getY());

            absTargetPos = drive.getStatus()
                    .currentPosition()
                    .getTranslation()
                    .plus(relativeTarget.rotateBy(
                            Rotation2d.fromDegrees((drive.getStatus().heading() + 180))));

            // SmartDashboard.putString("target pos", absTargetPos.toString());

            // drive.setCurrentPosition(
            //         new Pose2d(
            //                 absTargetPos.minus(
            //                         relativeTarget.rotateBy(
            //                                 Rotation2d.fromDegrees(drive.getHeading() + 180))),
            //                 Rotation2d.fromDegrees(drive.getHeading())));

            return true;

        } catch (Exception e) {
            if (!(e instanceof AprilTagGeneralCheckedException)) {
                // Logger.get(Category.CAMERA).logRaw(Severity.WARNING, "Unable to use camera");
                // LoggerExceptionUtils.logException(e);
            }
            return false;
        }
    }

    private void updateRelativeTranslation2d() {
        try {
            Transform3d transform3d = camera.getStatus().speakerTagTransform().get();
            relativeTranslation2d = new Translation2d(transform3d.getX(), transform3d.getY());
        } catch (Exception e) {
            if (!(e instanceof AprilTagGeneralCheckedException)) {
                // Logger.get(Category.CAMERA).logRaw(Severity.WARNING, "Unable to use camera");
                // LoggerExceptionUtils.logException(e);
            }
            relativeTranslation2d = absTargetPos
                    .minus(drive.getStatus().currentPosition().getTranslation())
                    .rotateBy(Rotation2d.fromDegrees(-drive.getStatus().heading() - 180));
        }
    }

    public void update() {
        updateAlliance();
        updateTarget();
        updateRelativeTranslation2d();
        // SmartDashboard.putString("translation", relativeTranslation2d.toString());
        // SmartDashboard.putNumber("Tag Dist", relativeTranslation2d.getNorm());
    }

    public Rotation2d getHeadingToTarget() {

        // Calculates the required heading to face the last valid updating of the rotationLockTarget
        // Undoes the rotation to find a new relative translation between the robot and target, even
        // if the target is not currently seen
        // Calculated the heading the robot needs to face from this translation

        double val = relativeTranslation2d.getAngle().getDegrees()
                + drive.getStatus().heading();
        // SmartDashboard.putNumber(
        //         "relativeTranslation2d angle", relativeTranslation2d.getAngle().getDegrees());
        // SmartDashboard.putNumber(
        //         "heading angle", Rotation2d.fromDegrees(drive.getHeading()).getDegrees());
        // SmartDashboard.putNumber("output heading", val);
        return Rotation2d.fromDegrees(val);
    }

    public double getShooterPower() throws AprilTagGeneralCheckedException {
        double val = VisionPIDProcedure.getBestPowerToUse(relativeTranslation2d.getNorm());
        // SmartDashboard.putNumber("shooter power", val);
        return val;
    }

    public double getArmAngle() throws AprilTagGeneralCheckedException {
        double val = VisionPIDProcedure.getBestArmAngleToUse(relativeTranslation2d.getNorm());
        // SmartDashboard.putNumber("arm angle", val);
        return val;
    }
}
