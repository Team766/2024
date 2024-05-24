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
import edu.wpi.first.math.geometry.Rotation2d;

public class RotateAndShootNow extends Procedure {

    private final Drive drive;
    private final Superstructure superstructure;
    private final Shooter shooter;
    private final Intake intake;

    private final VisionSpeakerHelper visionSpeakerHelper;

    public RotateAndShootNow(
            Drive drive, Superstructure superstructure, Shooter shooter, Intake intake) {
        super(reservations(drive, superstructure, shooter, intake));
        this.drive = drive;
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
        visionSpeakerHelper = new VisionSpeakerHelper();
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        drive.setGoal(new Drive.StopDrive());

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

        superstructure.setGoal(new Shoulder.RotateToPosition(armAngle));
        drive.setGoal(new Drive.FieldOrientedVelocityWithRotationTarget(0, 0, heading));
        // shooter.shoot(power);

        context.waitForConditionOrTimeout(
                () -> getStatus(Shoulder.Status.class).get().isNearTo(armAngle), 0.5);
        context.waitForConditionOrTimeout(
                () -> drive.getStatus().isAtRotationTarget(heading.getDegrees()), 3.0);
        drive.setGoal(new Drive.StopDrive());

        context.runSync(new ShootVelocityAndIntake(shooter, intake));
    }
}
