package com.team766.robot.reva.VisionUtil;

import java.util.Optional;
import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.robot.reva.Robot;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class VisionSpeakerHelper {

	int tagId;
    double angle;
	GrayScaleCamera camera;

    public VisionSpeakerHelper() {
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
    }

	// TODO: make this also have the dead reckoning like drive
	public Translation2d getTranslation2d() {
		try {
			Transform3d transform3d = GrayScaleCamera.getBestTargetTransform3d(camera.getTrackedTargetWithID(tagId));
			return new Translation2d(transform3d.getX(), transform3d.getY());
		} catch(AprilTagGeneralCheckedException e) {
			return null;
		}	
	}

	public double getShooterPower() throws AprilTagGeneralCheckedException {
		return VisionPIDProcedure.getBestPowerToUse(getTranslation2d().getNorm());
	}

	public double getArmAngle() throws AprilTagGeneralCheckedException {
		return VisionPIDProcedure.getBestArmAngleToUse(getTranslation2d().getNorm());
	}
}
