package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;

public class RotateAndShootNow extends Procedure {

    VisionSpeakerHelper visionSpeakerHelper;

    public RotateAndShootNow() {
        visionSpeakerHelper = new VisionSpeakerHelper(Robot.drive);
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);

        Robot.drive.stopDrive();

        double power;
        double armAngle;

        visionSpeakerHelper.update(context);

        try {
            power = visionSpeakerHelper.getShooterPower();
            armAngle = visionSpeakerHelper.getArmAngle();
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        Robot.shoulder.rotate(armAngle);
        Robot.drive.controlFieldOrientedWithRotationTarget(
                0, 0, visionSpeakerHelper.getHeadingToTarget());
        Robot.shooter.shoot(power);

        context.waitFor(Robot.shoulder::isFinished);
        context.waitFor(Robot.drive::isAtRotationTarget);

        context.releaseOwnership(Robot.shooter);
        new ShootVelocityAndIntake(power).run(context);
    }
}
