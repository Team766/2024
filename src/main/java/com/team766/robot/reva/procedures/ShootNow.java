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

        Transform3d toUse;

        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double y = toUse.getY();

        angle = Math.atan(y / x);
        anglePID.setSetpoint(angle);
        log("Finihed constructor");
    }

    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);
        context.takeOwnership(Robot.intake);
        Transform3d toUse;
        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        /*
         * Should we calculate these before angleing the robot or after?
         */
        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));
        
        log("Found distance: " + distanceOfRobotToTag);

        double power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
        double armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);

        log("Found power + armangle: " + power + " : " + armAngle);

        // while (anglePID.getOutput() != 0) {
        //     context.yield();

        //     try {
        //         toUse = getTransform3dOfRobotToTag();

        //         anglePID.calculate(toUse.getRotation().getZ());
        //     } catch (AprilTagGeneralCheckedException e) {
        //         continue;
        //     }

        //     Robot.drive.controlRobotOriented(0, 0, anglePID.getOutput());
        // }

        Robot.shoulder.rotate(armAngle);

        while (!Robot.shoulder.isFinished()) {
            context.yield();
        }

        // Placeholder method calls for procedure to be made

        Robot.shooter.shootPower(power);
        context.waitForSeconds(2);
        new IntakeIn().run(context);


        context.waitForSeconds(1.1);

        Robot.shooter.stop();
        new IntakeStop().run(context);
        Robot.shoulder.rotate(0);
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());

        int tagIdInCamera = toUse.getTagIdOfBestTarget();

        // this is the tag we will be using for testing in the time being. later we will need to set
        // based on alliance color
        if (tagId == tagIdInCamera) {
            log("Finihed getting transform 3d");
            return robotToTag;
        }

        throw new AprilTagGeneralCheckedException("Could not find tag with the correct tagId. Currently found: " + tagIdInCamera);
        
    }
}
