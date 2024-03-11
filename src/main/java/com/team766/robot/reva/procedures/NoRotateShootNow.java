package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;

public class NoRotateShootNow extends Procedure {

    VisionSpeakerHelper visionSpeakerHelper;
    boolean amp;

    public NoRotateShootNow(boolean amp) {
        this.amp = amp;
        visionSpeakerHelper = new VisionSpeakerHelper(Robot.drive);
    }

    public void run(Context context) {
        if (!amp) {
            context.takeOwnership(Robot.drive);
            context.takeOwnership(Robot.shooter);
            context.takeOwnership(Robot.shoulder);

            Robot.drive.stopDrive();

            context.releaseOwnership(Robot.drive);

            double power;
            double armAngle;

            visionSpeakerHelper.update();

            try {
                power = visionSpeakerHelper.getShooterPower();
                armAngle = visionSpeakerHelper.getArmAngle();
            } catch (AprilTagGeneralCheckedException e) {
                LoggerExceptionUtils.logException(e);
                return;
            }

            Robot.shoulder.rotate(armAngle);

            // start shooting now while waiting for shoulder, stopped in ShootVelocityAndIntake
            Robot.shooter.shoot(power);

            context.waitForConditionOrTimeout(Robot.shoulder::isFinished, 0.5);

            context.releaseOwnership(Robot.shooter);
            context.releaseOwnership(Robot.shoulder);
            context.runSync(new ShootVelocityAndIntake(power));

        } else {
            // context.takeOwnership(Robot.shooter);

            // context.takeOwnership(Robot.shoulder);

            // Robot.shooter.shoot(3000);
            // Robot.shoulder.rotate(ShoulderPosition.AMP);

            // context.waitFor(Robot.shoulder::isFinished);

            // context.releaseOwnership(Robot.shoulder);
            // context.releaseOwnership(Robot.shooter);

            context.runSync(new ShootVelocityAndIntake(3000));
        }
    }
}
