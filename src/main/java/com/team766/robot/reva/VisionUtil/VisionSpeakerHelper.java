package com.team766.robot.reva.VisionUtil;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class VisionSpeakerHelper {

    int tagId;
    double angle;
    GrayScaleCamera camera;
    SwerveDrive drive;
    Translation2d absTargetPos;
    Translation2d relativeTranslation2d;

    // TODO: make this static
    public VisionSpeakerHelper(SwerveDrive drive) {
        camera = Robot.forwardApriltagCamera.getCamera();
        this.drive = drive;
    }

    private void updateAlliance() {
        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = VisionConstants.MAIN_BLUE_SPEAKER_TAG;
                absTargetPos = VisionConstants.MAIN_BLUE_SPEAKER_TAG_POS;
            } else if (alliance.get().equals(Alliance.Red)) {
                tagId = VisionConstants.MAIN_RED_SPEAKER_TAG;
                absTargetPos = VisionConstants.MAIN_RED_SPEAKER_TAG_POS;
            }
        } else {
            tagId = -1;
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

            Transform3d transform3d =
                    GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));
            Translation2d relativeTarget =
                    new Translation2d(transform3d.getX(), transform3d.getY());

            absTargetPos =
                    drive.getCurrentPosition()
                            .getTranslation()
                            .plus(
                                    relativeTarget.rotateBy(
                                            Rotation2d.fromDegrees((drive.getHeading() + 180))));

            // SmartDashboard.putString("target pos", absTargetPos.toString());

            // context.takeOwnership(drive);

            // drive.setCurrentPosition(
            //         new Pose2d(
            //                 absTargetPos.minus(
            //                         relativeTarget.rotateBy(
            //                                 Rotation2d.fromDegrees(drive.getHeading() + 180))),
            //                 Rotation2d.fromDegrees(drive.getHeading())));

            // context.releaseOwnership(drive);

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
            Transform3d transform3d =
                    GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));
            relativeTranslation2d = new Translation2d(transform3d.getX(), transform3d.getY());
        } catch (Exception e) {
            if (!(e instanceof AprilTagGeneralCheckedException)) {
                // Logger.get(Category.CAMERA).logRaw(Severity.WARNING, "Unable to use camera");
                // LoggerExceptionUtils.logException(e);
            }
            relativeTranslation2d =
                    absTargetPos
                            .minus(drive.getCurrentPosition().getTranslation())
                            .rotateBy(Rotation2d.fromDegrees(-drive.getHeading() - 180));
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

        double val = relativeTranslation2d.getAngle().getDegrees() + drive.getHeading();
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
