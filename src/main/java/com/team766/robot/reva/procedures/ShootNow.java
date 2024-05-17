package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.framework.Context;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.ShootingProcedureStatus.Status;
import edu.wpi.first.math.geometry.Transform3d;

public class ShootNow extends VisionPIDProcedure {

    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final ForwardApriltagCamera forwardApriltagCamera;

    private double angle;

    public ShootNow(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(reservations(drive, shoulder, shooter, intake));
        this.drive = drive;
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
        this.forwardApriltagCamera = forwardApriltagCamera;
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        updateStatus(new ShootingProcedureStatus(Status.RUNNING));
        drive.setGoal(new Drive.StopDrive());

        Transform3d toUse;

        context.waitForConditionOrTimeout(() -> seesTarget(), 1.0);

        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double y = toUse.getY();

        anglePID.setSetpoint(0);

        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));

        if (distanceOfRobotToTag
                > VisionPIDProcedure.scoringPositions
                        .get(VisionPIDProcedure.scoringPositions.size() - 1)
                        .distanceFromCenterApriltag()) {
            updateStatus(new ShootingProcedureStatus(Status.OUT_OF_RANGE));
            return;
        }
        double power;
        double armAngle;
        try {
            power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
            armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        shooter.setGoal(new Shooter.ShootAtSpeed(power));

        shoulder.setGoal(new Shoulder.RotateToPosition(armAngle));

        angle = Math.atan2(y, x);

        anglePID.calculate(angle);

        while (Math.abs(anglePID.getOutput()) > 0.075) {
            context.yield();

            // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
            // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);
            try {
                toUse = getTransform3dOfRobotToTag();

                y = toUse.getY();
                x = toUse.getX();

                angle = Math.atan2(y, x);

                anglePID.calculate(angle);
            } catch (AprilTagGeneralCheckedException e) {
                continue;
            }

            drive.setGoal(new Drive.RobotOrientedVelocity(0, 0, -anglePID.getOutput()));
        }

        drive.setGoal(new Drive.StopDrive());

        // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
        // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);

        context.waitForConditionOrTimeout(() -> shoulder.getStatus().isNearTo(armAngle), 1);

        updateStatus(new ShootingProcedureStatus(Status.FINISHED));
        context.runSync(new ShootVelocityAndIntake(power, shooter, intake));
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        return forwardApriltagCamera.getStatus().speakerTagTransform().get();
    }

    private boolean seesTarget() {
        return forwardApriltagCamera.getStatus().speakerTagTransform().hasValue();
    }
}
