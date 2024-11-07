package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.orin.NoTagFoundError;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class DriverShootNow extends VisionPIDProcedure {

    private int tagId;
    private double angle;

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = VisionConstants.MAIN_BLUE_SPEAKER_TAG;
            } else if (alliance.get().equals(Alliance.Red)) {
                tagId = VisionConstants.MAIN_RED_SPEAKER_TAG;
            }
        } else {
            tagId = -1;
        }

        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shoulder);

        Robot.lights.signalStartingShootingProcedure();
        Robot.drive.stopDrive();

        Transform3d toUse;
        try {
            /* Interchange the following two lines for Orin vs. Orange Pi! */
            // toUse = getTransform3dOfRobotToTag();
            toUse = getTransform3dOfRobotToTagOrin();
        } catch (NoTagFoundError e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double z = toUse.getZ();

        anglePID.setSetpoint(0);

        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getZ(), 2));
            
        log("DIST: " + distanceOfRobotToTag);
        if (distanceOfRobotToTag
                > VisionPIDProcedure.scoringPositions
                        .get(VisionPIDProcedure.scoringPositions.size() - 1)
                        .distanceFromCenterApriltag()) {
            Robot.lights.signalShooterOutOfRange();
        }
        // double power;
        double armAngle;
        try {
            // power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
            armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        // Robot.shooter.shoot(power);

        Robot.shoulder.rotate(armAngle);
        log("ArmAngle: " + armAngle);

        angle = Math.atan2(x,z);

        log("ROBOT ANGLE: " + angle);

        anglePID.calculate(angle);

        log("ANGLE PID: " + anglePID.getOutput());

        while (Math.abs(anglePID.getOutput()) > 0.075) {
            context.yield();

            // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
            // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);
            try {
                toUse = getTransform3dOfRobotToTagOrin();

                z = toUse.getZ();
                x = toUse.getX();

                angle = Math.atan2(x,z);

                anglePID.calculate(angle);
            } catch (NoTagFoundError e) {
                continue;
            }

            Robot.drive.controlRobotOriented(0, 0, anglePID.getOutput());
        }

        Robot.drive.stopDrive();
        context.releaseOwnership(Robot.drive);

        // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
        // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);

        context.waitForConditionOrTimeout(() -> Robot.shoulder.isFinished(), 1);

        Robot.lights.signalFinishingShootingProcedure();
        context.runSync(new DriverShootVelocityAndIntake());
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        return GrayScaleCamera.getBestTargetTransform3d(toUse.getTrackedTargetWithID(tagId));
    }

    private Transform3d getTransform3dOfRobotToTagOrin() throws NoTagFoundError {
        AprilTag tag = Robot.orin.getTagById(tagId);

        log(tag.toString());
        Pose3d pose = tag.pose;

        Transform3d poseNew =
                new Transform3d(pose.getX(), pose.getY(), pose.getZ(), new Rotation3d());
        return poseNew;
    }
}
