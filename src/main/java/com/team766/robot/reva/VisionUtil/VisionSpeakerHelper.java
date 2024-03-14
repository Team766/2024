package com.team766.robot.reva.VisionUtil;

import java.util.Optional;
import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionSpeakerHelper {

	int tagId;
    double angle;
	GrayScaleCamera camera;
	Drive drive;
	Translation2d absTargetPos = new Translation2d(0, 0);
	boolean targetTranslationFlip;


    public VisionSpeakerHelper(Drive drive) {
        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = 7;
            } else if (alliance.get().equals(Alliance.Red)) {
                tagId = 4;
            }
        } else {
            tagId = -1;
        }

		camera = Robot.forwardApriltagCamera.getCamera();
		this.drive = drive;
		targetTranslationFlip = (DriverStation.getAlliance().get() == Alliance.Blue);
    }

	public void updateTarget() {
		try {

			// Calculates the absolute position of the target according to odometry
            // Rotates the direction of the relative translation to correct for robot orientation:
            // Shooter camera is on back of the robot, red alliance's gyro is 180 - absolute rotation
            // Sticks around even when there is no new valid relativeTarget

			Transform3d transform3d = GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));
			Translation2d relativeTarget = new Translation2d(transform3d.getX(), transform3d.getY());
			absTargetPos = drive.getCurrentPosition().getTranslation().plus(
                relativeTarget.rotateBy(Rotation2d.fromDegrees(
                    targetTranslationFlip ? (drive.getHeading() + 180) : (-drive.getHeading()))));
			// Logger.get(Category.CAMERA).logRaw(Severity.INFO, "target pos:" + absTargetPos);
			SmartDashboard.putString("target pos", absTargetPos.toString());
		} catch(AprilTagGeneralCheckedException e) {
			// LoggerExceptionUtils.logException(e);
			return;
		}

	}

	public Translation2d getTranslation2d() {
		try {
			Transform3d transform3d = GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));
			return new Translation2d(transform3d.getX(), transform3d.getY());
		} catch(AprilTagGeneralCheckedException e) {
			return absTargetPos.minus(drive.getCurrentPosition().getTranslation()).rotateBy(Rotation2d.fromDegrees(
                    targetTranslationFlip ? (-drive.getHeading() - 180) : (drive.getHeading())));
		}	
	}

	public Rotation2d getHeadingToTarget() {

		// Calculates the required heading to face the last valid updating of the rotationLockTarget
        // Undoes the rotation to find a new relative translation between the robot and target, even if the target is not currently seen
        // Calculated the heading the robot needs to face from this translation
		
		Rotation2d val = getTranslation2d().getAngle().plus(Rotation2d.fromDegrees(drive.getHeading()));
		SmartDashboard.putNumber("output heading", val.getDegrees());
		return val;
	}

	public double getShooterPower() throws AprilTagGeneralCheckedException {
		double val = VisionPIDProcedure.getBestPowerToUse(getTranslation2d().getNorm());
		SmartDashboard.putNumber("shooter power", val);
		return val;
	}

	public double getArmAngle() throws AprilTagGeneralCheckedException {
		double val = VisionPIDProcedure.getBestArmAngleToUse(getTranslation2d().getNorm());
		SmartDashboard.putNumber("arm angle", val);
		return val;

	}
}
