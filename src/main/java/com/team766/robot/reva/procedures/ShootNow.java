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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
    }

    // TODO: ADD LED COMMANDS BASED ON EXCEPTIONS
    public void run(Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.shooter);
        context.takeOwnership(Robot.shoulder);
        context.takeOwnership(Robot.intake);

        // Robot.drive.stopDrive();

        Transform3d toUse;
        try {
            toUse = getTransform3dOfRobotToTag();

        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        double x = toUse.getX();
        double y = toUse.getY();

        anglePID.setSetpoint(0);

        /*
         * Should we calculate these before angleing the robot or after?
         * 3/9 consensous: before
         */

        double distanceOfRobotToTag =
                Math.sqrt(Math.pow(toUse.getX(), 2) + Math.pow(toUse.getY(), 2));

        double power;
        double armAngle;
        try {
            power = VisionPIDProcedure.getBestPowerToUse(distanceOfRobotToTag);
            armAngle = VisionPIDProcedure.getBestArmAngleToUse(distanceOfRobotToTag);
        } catch (AprilTagGeneralCheckedException e) {
            LoggerExceptionUtils.logException(e);
            return;
        }

        Robot.shoulder.rotate(armAngle);

        // double toAdd;

        // if(toUse.getRotation().getZ() < 0){
        //     toAdd = -3;
        // } else{
        //     toAdd = 3;
        // }

        // anglePID.calculate(toUse.getRotation().getZ() + toAdd);

        angle = Math.atan2(y, x);

        anglePID.calculate(angle);

        log("eee: " + toUse.getRotation().getZ());

        while (Math.abs(anglePID.getOutput()) > 0.04) {
            context.yield();

            SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
            SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);
            try {
                toUse = getTransform3dOfRobotToTag();

                y = toUse.getY();
                x = toUse.getX();

                angle = Math.atan2(y, x);

                anglePID.calculate(angle);
            } catch (AprilTagGeneralCheckedException e) {
                continue;
            }

            Robot.drive.controlRobotOriented(0, 0, -anglePID.getOutput());
        }

        SmartDashboard.putNumber("[ANGLE PID OUTPUT]", anglePID.getOutput());
        SmartDashboard.putNumber("[ANGLE PID ROTATION]", angle);

        context.waitFor(() -> Robot.shoulder.isFinished());

        log("Shoulder moved");

        context.releaseOwnership(Robot.shooter);
        context.releaseOwnership(Robot.intake);
        new ShootVelocityAndIntake(power).run(context);

        log("Done with power");
    }

    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        return GrayScaleCamera.getBestTargetTransform3d(toUse.getTrackedTargetWithID(tagId));
    }
}