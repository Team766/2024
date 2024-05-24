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
import com.team766.robot.reva.mechanisms.Superstructure;
import com.team766.robot.reva.procedures.ShootingProcedureStatus.Status;
import edu.wpi.first.math.geometry.Transform3d;

public class ShootNow extends VisionPIDProcedure {

    private final Drive drive;
    private final Superstructure superstructure;
    private final Shooter shooter;
    private final Intake intake;

    private double angle;

    public ShootNow(Drive drive, Superstructure superstructure, Shooter shooter, Intake intake) {
        super(reservations(drive, superstructure, shooter, intake));
        this.drive = drive;
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
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

        superstructure.setGoal(new Shoulder.RotateToPosition(armAngle));

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

        context.waitForConditionOrTimeout(
                () -> getStatus(Shoulder.Status.class).get().isNearTo(armAngle), 1);

        updateStatus(new ShootingProcedureStatus(Status.FINISHED));
        context.runSync(new ShootVelocityAndIntake(power, shooter, intake));
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        return getStatus(ForwardApriltagCamera.Status.class)
                .get()
                .speakerTagTransform()
                .get();
    }

    private boolean seesTarget() {
        return getStatus(ForwardApriltagCamera.Status.class)
                .get()
                .speakerTagTransform()
                .hasValue();
    }
}
