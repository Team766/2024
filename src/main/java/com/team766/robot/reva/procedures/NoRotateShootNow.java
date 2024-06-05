package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

@CollectReservations
public class NoRotateShootNow extends MagicProcedure<NoRotateShootNow_Reservations> {

    @Reserve Drive drive;

    @Reserve Superstructure superstructure;

    @Reserve Shooter shooter;

    private final VisionSpeakerHelper visionSpeakerHelper = new VisionSpeakerHelper();
    private final boolean amp;

    public NoRotateShootNow(boolean amp) {
        this.amp = amp;
    }

    public void run(Context context) {
        if (!amp) {
            drive.setGoal(new Drive.StopDrive());

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

            superstructure.setGoal(new Shoulder.RotateToPosition(armAngle));

            // start shooting now while waiting for shoulder, stopped in ShootVelocityAndIntake
            shooter.setGoal(new Shooter.ShootAtSpeed(power));

            context.waitForConditionOrTimeout(
                    () -> getStatus(Shoulder.Status.class).get().isNearTo(armAngle), 0.5);

            context.runSync(new ShootVelocityAndIntake(power));

        } else {
            // Robot.shooter.shoot(3000);
            // Robot.shoulder.rotate(ShoulderPosition.AMP);

            // context.waitFor(Robot.shoulder::isFinished);

            context.runSync(new ShootVelocityAndIntake(3000));
        }
    }
}
