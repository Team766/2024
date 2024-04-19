package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import edu.wpi.first.math.geometry.Rotation2d;

public class RotateAndShootNow extends Procedure {

    VisionSpeakerHelper visionSpeakerHelper;

    public RotateAndShootNow() {
        visionSpeakerHelper = new VisionSpeakerHelper(Robot.drive);
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);
        context.takeOwnership(Robot.drive);

        Robot.drive.stopDrive();
        // context.releaseOwnership(Robot.drive);

        // double power;
        double armAngle;
        Rotation2d heading;

        visionSpeakerHelper.update();

        try {
            // power = visionSpeakerHelper.getShooterPower();
            armAngle = visionSpeakerHelper.getArmAngle();
            heading = visionSpeakerHelper.getHeadingToTarget();
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        // context.takeOwnership(Robot.drive);
        Robot.shoulder.rotate(armAngle);
        Robot.drive.controlFieldOrientedWithRotationTarget(0, 0, heading);
        // Robot.shooter.shoot(power);
        context.releaseOwnership(Robot.shoulder);

        context.waitForConditionOrTimeout(Robot.shoulder::isFinished, 0.5);
        context.waitForConditionOrTimeout(Robot.drive::isAtRotationTarget, 3.0);
        Robot.drive.stopDrive();

        // context.releaseOwnership(Robot.shoulder);
        context.releaseOwnership(Robot.drive);
        context.releaseOwnership(Robot.shooter);
        context.runSync(new ShootVelocityAndIntake());
    }
}
