package com.team766.robot.reva.procedures;

import java.util.Optional;
import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class ShootNow extends VisionPIDProcedure {

	int tagId;

	public ShootNow() {
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
	}
	
	public void run(Context context) {
		Transform3d toUse;
		try {
			toUse = getTransform3dOfRobotToTag();
			
		} catch (AprilTagGeneralCheckedException e) {
			return;
		}

		double distanceOfRobotToTag = Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));
		
		double power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
		double armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);

		//Placeholder method calls for procedures to be made
		Robot.shooter.shootPower(power);
		Robot.shoulder.rotate(armAngle);

	}
	
	private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());

        int tagIdInCamera = toUse.getTagIdOfBestTarget();

        // this is the tag we will be using for testing in the time being. later we will need to set
        // based on alliance color
        if (tagId == tagIdInCamera) {
            return robotToTag;
        }

        throw new AprilTagGeneralCheckedException("Could not find tag with the correct tagId");
    }
}
