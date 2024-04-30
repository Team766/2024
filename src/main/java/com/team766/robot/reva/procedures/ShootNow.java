package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import com.team766.robot.reva.constants.VisionConstants;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class ShootNow extends VisionPIDProcedure {

    private final Drive drive;
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final Lights lights;
    private final ForwardApriltagCamera forwardApriltagCamera;

    private int tagId;
    private double angle;

    public ShootNow(
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
        this.forwardApriltagCamera = forwardApriltagCamera;
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {

        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = VisionConstants.MAIN_BLUE_SPEAKER_TAG;
            } else if (alliance.get().equals(Alliance.Red)) {
                tagId = VisionConstants.MAIN_RED_SPEAKER_TAG;
            }
        } else {
            tagId = -1;
        }

        lights.signalStartingShootingProcedure();
        drive.stopDrive();

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
            lights.signalShooterOutOfRange();
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

        shooter.shoot(power);

        shoulder.rotate(armAngle);

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

            drive.controlRobotOriented(0, 0, -anglePID.getOutput());
        }

        drive.stopDrive();

        // SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
        // SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);

        context.waitForConditionOrTimeout(() -> shoulder.isFinished(), 1);

        lights.signalFinishingShootingProcedure();
        context.runSync(new ShootVelocityAndIntake(power, shooter, intake, lights));
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = forwardApriltagCamera.getCamera();

        return GrayScaleCamera.getBestTargetTransform3d(toUse.getTrackedTargetWithID(tagId));
    }

    private boolean seesTarget() {
        GrayScaleCamera toUse = forwardApriltagCamera.getCamera();

        try {
            toUse.getTrackedTargetWithID(tagId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
