package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;

public class NoRotateShootNow extends Procedure {

    VisionSpeakerHelper visionSpeakerHelper;

    public NoRotateShootNow() {
        visionSpeakerHelper = new VisionSpeakerHelper(Robot.drive);
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);

        Robot.drive.stopDrive();

        context.releaseOwnership(Robot.drive);

        double power;
        double armAngle;

        try {
            power = visionSpeakerHelper.getShooterPower();
            armAngle = visionSpeakerHelper.getArmAngle();
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        Robot.shoulder.rotate(armAngle);
        Robot.shooter.shoot(power);

        context.waitFor(Robot.shoulder::isFinished);

        context.releaseOwnership(Robot.shooter);
        new ShootVelocityAndIntake(power).run(context);
    }
}
