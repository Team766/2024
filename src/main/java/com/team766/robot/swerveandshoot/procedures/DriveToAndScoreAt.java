package com.team766.robot.swerveandshoot.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.ViSIONbase.ScoringPosition;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.swerveandshoot.Robot;
import edu.wpi.first.math.geometry.Transform3d;
import com.team766.robot.swerveandshoot.VisionPIDControllers;


public class DriveToAndScoreAt extends Procedure {

	private ScoringPosition score;
	private double lastX;
	private double lastY;

	public DriveToAndScoreAt(ScoringPosition score){
		this.score = score;
	}

    public void run(final Context context) {
		//the intent of this while loop is to make it so the code runs until this is met. However, right now, this wont work (obvious). Lets fix
		while(VisionPIDControllers.xPID.getOutput() + VisionPIDControllers.yPID.getOutput() != 0){
			VisionPIDControllers.yPID.setSetpoint(score.y_position);
			VisionPIDControllers.xPID.setSetpoint(score.x_position);
			Transform3d robotToTag;
			double turnConstant = 0;

			try {
				robotToTag = this.getTransform3dOfRobotToTag();

				VisionPIDControllers.yPID.calculate(robotToTag.getY());
				VisionPIDControllers.xPID.calculate(robotToTag.getX());

				lastX = robotToTag.getX();
				lastY = robotToTag.getY();

				// If it is more that four degrees off...
				if (Math.abs(robotToTag.getRotation().getZ()) > 4) {

				} else {
					if (robotToTag.getRotation().getZ() < 0) {
						turnConstant = -0.02;
					} else {
						turnConstant = 0.02;
					}
				}
			} catch (AprilTagGeneralCheckedException e) {

				VisionPIDControllers.yPID.calculate(lastY);
				VisionPIDControllers.xPID.calculate(lastX);

				turnConstant = 0; // needed?
			}

			Robot.tempShooter.setAngle(score.angle);
			Robot.tempShooter.runMotors(score.power);

			Robot.drive.controlRobotOriented(VisionPIDControllers.yPID.getOutput(), -VisionPIDControllers.xPID.getOutput(), turnConstant);


		}
		Robot.tempShooter.shoot();
	}

	/**
     * This method will return the transform3d of the robot to the tag.
     * It checks to make sure the tag is of the correct tag ID (according to the current alliance),
     * where it will then give that transform3d.
     * This is more backend-ey, so could it go into a deeper class that we could use for abstraction?
     *
     * @return Transform3d of the robot to the tag
     * @throws AprilTagGeneralCheckedException if the tag is not found by any camera
     * @author Max Spier, 1/7/2024
     */
    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());

        return robotToTag;
    }
}
