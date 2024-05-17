package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.framework.SubsystemStatus;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class NoRotateShootNow extends Procedure {

    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final VisionSpeakerHelper visionSpeakerHelper;
    private final boolean amp;

    public NoRotateShootNow(
            boolean amp,
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            SubsystemStatus<ForwardApriltagCamera.Status> forwardApriltagCamera) {
        super(reservations(drive, shooter, shoulder, intake));
        this.drive = drive;
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
        this.amp = amp;
        visionSpeakerHelper = new VisionSpeakerHelper(drive, forwardApriltagCamera);
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

            shoulder.setGoal(new Shoulder.RotateToPosition(armAngle));

            // start shooting now while waiting for shoulder, stopped in ShootVelocityAndIntake
            shooter.setGoal(new Shooter.ShootAtSpeed(power));

            context.waitForConditionOrTimeout(() -> shoulder.getStatus().isNearTo(armAngle), 0.5);

            context.runSync(new ShootVelocityAndIntake(power, shooter, intake));

        } else {
            // Robot.shooter.shoot(3000);
            // Robot.shoulder.rotate(ShoulderPosition.AMP);

            // context.waitFor(Robot.shoulder::isFinished);

            context.runSync(new ShootVelocityAndIntake(3000, shooter, intake));
        }
    }
}
