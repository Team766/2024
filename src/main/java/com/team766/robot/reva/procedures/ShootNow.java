package com.team766.robot.reva.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.framework.Context;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.VisionUtil.VisionPIDProcedure;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;
import org.photonvision.targeting.PhotonTrackedTarget;

public class ShootNow extends VisionPIDProcedure {

    int tagId;
    double angle;

    public ShootNow() {
        Optional<Alliance> alliance = DriverStation.getAlliance();

        if (alliance.isPresent()) {
            if (alliance.get().equals(Alliance.Blue)) {
                tagId = 7;
            } else if (alliance.get().equals(Alliance.Red)) {
                tagId = 4;
            }
        } else {
            tagId = -1;
        }
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);
        context.takeOwnership(Robot.intake);

        Robot.drive.stopDrive();

        Transform3d toUse;
        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double y = toUse.getY();

        angle = Math.atan2(y, x);
        anglePID.setSetpoint(angle);

        /*
         * Should we calculate these before angleing the robot or after?
         */
        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));
        
        log("Found distance: " + distanceOfRobotToTag);

        double power;
        double armAngle;
        try {
            power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
            armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);

            Robot.shooter.shootPower(power);
            Robot.shoulder.rotate(armAngle);

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            
        }
        while (anglePID.getOutput() != 0) {
            context.yield();

            try {
                toUse = getTransform3dOfRobotToTag();

                anglePID.calculate(toUse.getRotation().getZ());
            } catch (AprilTagGeneralCheckedException e) {
                continue;
            }

            Robot.drive.controlRobotOriented(0, 0, anglePID.getOutput());
        }
        
        context.waitFor(() -> Robot.shoulder.isFinished());

        context.waitForSeconds(1);
        new IntakeIn().run(context);


        context.waitForSeconds(3);

        Robot.shooter.stop();
        new IntakeStop().run(context);
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        return GrayScaleCamera.getBestTargetTransform3d(toUse.getTrackedTargetWithID(tagId));
    }
}
