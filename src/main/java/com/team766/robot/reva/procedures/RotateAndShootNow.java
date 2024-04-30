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
import edu.wpi.first.math.geometry.Rotation2d;

public class RotateAndShootNow extends Procedure {

    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final Lights lights;

    private final VisionSpeakerHelper visionSpeakerHelper;

    public RotateAndShootNow(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Lights lights,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(reservations(drive, shoulder, shooter, intake));
        this.drive = drive;
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
        this.lights = lights;
        visionSpeakerHelper = new VisionSpeakerHelper(drive, forwardApriltagCamera);
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        drive.stopDrive();

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

        shoulder.rotate(armAngle);
        drive.controlFieldOrientedWithRotationTarget(0, 0, heading);
        // shooter.shoot(power);

        context.waitForConditionOrTimeout(shoulder::isFinished, 0.5);
        context.waitForConditionOrTimeout(drive::isAtRotationTarget, 3.0);
        drive.stopDrive();

        context.runSync(new ShootVelocityAndIntake(shooter, intake, lights));
    }
}
