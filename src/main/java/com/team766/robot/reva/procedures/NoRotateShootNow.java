package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

public class NoRotateShootNow extends Procedure {

    private final Drive drive;
    private final Superstructure superstructure;
    private final Shooter shooter;
    private final Intake intake;
    private final VisionSpeakerHelper visionSpeakerHelper;
    private final boolean amp;

    public NoRotateShootNow(
            boolean amp,
            Drive drive,
            Superstructure superstructure,
            Shooter shooter,
            Intake intake) {
        super(reservations(drive, shooter, superstructure, intake));
        this.drive = drive;
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
        this.amp = amp;
        visionSpeakerHelper = new VisionSpeakerHelper();
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

            context.runSync(new ShootVelocityAndIntake(power, shooter, intake));

        } else {
            // Robot.shooter.shoot(3000);
            // Robot.shoulder.rotate(ShoulderPosition.AMP);

            // context.waitFor(Robot.shoulder::isFinished);

            context.runSync(new ShootVelocityAndIntake(3000, shooter, intake));
        }
    }
}
