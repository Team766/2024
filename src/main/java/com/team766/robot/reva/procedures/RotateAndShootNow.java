package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.VisionUtil.VisionSpeakerHelper;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class RotateAndShootNow extends Procedure {

    private final SwerveDrive drive;
    private final ArmAndClimber superstructure;
    private final Shooter shooter;
    private final Intake intake;

    private final VisionSpeakerHelper visionSpeakerHelper;

    public RotateAndShootNow(
            SwerveDrive drive, ArmAndClimber superstructure, Shooter shooter, Intake intake) {
        this.drive = reserve(drive);
        this.superstructure = reserve(superstructure);
        this.shooter = reserve(shooter);
        this.intake = reserve(intake);
        visionSpeakerHelper = new VisionSpeakerHelper();
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        drive.setRequest(new SwerveDrive.Stop());

        // double power;
        Shoulder.RotateToPosition armRequest;
        SwerveDrive.DriveRequest headingRequest;

        visionSpeakerHelper.update();

        try {
            // power = visionSpeakerHelper.getShooterPower();
            armRequest = new Shoulder.RotateToPosition(visionSpeakerHelper.getArmAngle());
            headingRequest =
                    new SwerveDrive.FieldOrientedVelocityWithRotationTarget(
                            0, 0, visionSpeakerHelper.getHeadingToTarget());
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        superstructure.setRequest(armRequest);
        drive.setRequest(headingRequest);
        // shooter.shoot(power);

        context.waitForConditionOrTimeout(armRequest::isDone, 0.5);
        context.waitForConditionOrTimeout(headingRequest::isDone, 3.0);
        drive.setRequest(new SwerveDrive.Stop());

        context.runSync(new ShootVelocityAndIntake(shooter, intake));
    }
}
