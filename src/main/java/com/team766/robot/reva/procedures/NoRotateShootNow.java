package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class NoRotateShootNow extends Procedure {

    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final Lights lights;
    private final VisionSpeakerHelper visionSpeakerHelper;
    private final boolean amp;

    public NoRotateShootNow(
            boolean amp,
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Lights lights,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(reservations(drive, shooter, shoulder));
        this.drive = drive;
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
        this.lights = lights;
        this.amp = amp;
        visionSpeakerHelper = new VisionSpeakerHelper(drive, forwardApriltagCamera);
    }

    public void run(Context context) {
        if (!amp) {
            drive.stopDrive();

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

            shoulder.rotate(armAngle);

            // start shooting now while waiting for shoulder, stopped in ShootVelocityAndIntake
            shooter.shoot(power);

            context.waitForConditionOrTimeout(shoulder::isFinished, 0.5);

            context.runSync(new ShootVelocityAndIntake(power, shooter, intake, lights));

        } else {
            // Robot.shooter.shoot(3000);
            // Robot.shoulder.rotate(ShoulderPosition.AMP);

            // context.waitFor(Robot.shoulder::isFinished);

            context.runSync(new ShootVelocityAndIntake(3000, shooter, intake, lights));
        }
    }
}
