package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;
import edu.wpi.first.math.geometry.Rotation2d;

@CollectReservations
public class RotateAndShootNow extends MagicProcedure<RotateAndShootNow_Reservations> {

    @Reserve Drive drive;

    @Reserve Superstructure superstructure;

    private final VisionSpeakerHelper visionSpeakerHelper = new VisionSpeakerHelper();

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

        context.runSync(new ShootVelocityAndIntake());
    }
}
