package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.constants.VisionConstants;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class ShootNow extends VisionPIDProcedure {

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
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);

        Robot.lights.signalStartingShootingProcedure();
        Robot.drive.stopDrive();

        Transform3d toUse;

        context.waitForConditionOrTimeout(() -> seesTarget(), 1.0);

        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double y = toUse.getY();

        anglePID.setSetpoint(0);

        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));

        if (distanceOfRobotToTag
                > VisionPIDProcedure.scoringPositions
                        .get(VisionPIDProcedure.scoringPositions.size() - 1)
                        .distanceFromCenterApriltag()) {
            Robot.lights.signalShooterOutOfRange();
        }
        double power;
        double armAngle;
        try {
            power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
            armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        Robot.shooter.shoot(power);

        Robot.shoulder.rotate(armAngle);

        angle = Math.atan2(y, x);

        anglePID.calculate(angle);

        while (Math.abs(anglePID.getOutput()) > 0.075) {
            context.yield();

            // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
            // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);
            try {
                toUse = getTransform3dOfRobotToTag();

                y = toUse.getY();
                x = toUse.getX();

                angle = Math.atan2(y, x);

                anglePID.calculate(angle);
            } catch (AprilTagGeneralCheckedException e) {
                continue;
            }

            Robot.drive.controlRobotOriented(0, 0, -anglePID.getOutput());
        }

        Robot.drive.stopDrive();

        // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
        // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);

        context.waitForConditionOrTimeout(() -> Robot.shoulder.isFinished(), 1);

        context.releaseOwnership(Robot.shooter);
        Robot.lights.signalFinishingShootingProcedure();
        context.runSync(new ShootVelocityAndIntake(power));
        context.releaseOwnership(Robot.drive);
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        return GrayScaleCamera.getBestTargetTransform3d(toUse.getTrackedTargetWithID(tagId));
    }

    private boolean seesTarget() {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        try {
            toUse.getTrackedTargetWithID(tagId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
