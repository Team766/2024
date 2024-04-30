package com.team766.robot.swerveandshoot.procedures;

import com.team766.ViSIONbase.AprilTagGeneralCheckedException;
import com.team766.ViSIONbase.GrayScaleCamera;
import com.team766.ViSIONbase.ScoringPosition;
import com.team766.framework.Context;
import com.team766.hal.RobotProvider;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.swerveandshoot.VisionPIDProcedure;
import com.team766.robot.swerveandshoot.mechanisms.ForwardApriltagCamera;
import com.team766.robot.swerveandshoot.mechanisms.TempShooter;
import edu.wpi.first.math.geometry.Transform3d;

public class DriveToAndScoreAt extends VisionPIDProcedure {
    private final Drive drive;
    private final TempShooter tempShooter;
    private final ForwardApriltagCamera forwardApriltagCamera;

    private final ScoringPosition score;
    private double lastX;
    private double lastY;

    private double timeLastSeen = -1;

    public DriveToAndScoreAt(
            ScoringPosition score,
            Drive drive,
            TempShooter tempShooter,
            ForwardApriltagCamera forwardApriltagCamera) {
        super(reservations(drive, tempShooter));
        this.score = score;
        this.drive = drive;
        this.tempShooter = tempShooter;
        this.forwardApriltagCamera = forwardApriltagCamera;
    }

    // button needs to be held down
    public void run(final Context context) {
        yPID.setSetpoint(score.y_position);
        xPID.setSetpoint(score.x_position);

        while (Math.abs(xPID.getOutput()) + Math.abs(yPID.getOutput()) != 0) {
            context.yield();

            Transform3d robotToTag;
            double turnConstant = 0;

            try {
                robotToTag = this.getTransform3dOfRobotToTag();

                timeLastSeen = RobotProvider.instance.getClock().getTime();

                yPID.calculate(robotToTag.getY());
                xPID.calculate(robotToTag.getX());

                lastX = robotToTag.getX();
                lastY = robotToTag.getY();

                // If it is more that four degrees off...
                if (Math.abs(robotToTag.getRotation().getZ()) > 4) {

                } else {
                    if (robotToTag.getRotation().getZ() < 0) {
                        turnConstant = -0.02;
                    } else {
                        turnConstant = 0.02;
                    }
                }
            } catch (AprilTagGeneralCheckedException e) {
                double time = RobotProvider.instance.getClock().getTime();

                if (time - timeLastSeen >= 1) {
                    drive.controlRobotOriented(0, 0, 0);
                } else {
                    turnConstant = 0; // needed?
                    yPID.calculate(lastY);
                    xPID.calculate(lastX);
                    drive.controlRobotOriented(yPID.getOutput(), -xPID.getOutput(), turnConstant);
                }
            }

            tempShooter.setAngle(score.angle);
            tempShooter.runMotors(score.speed);

            drive.controlRobotOriented(yPID.getOutput(), -xPID.getOutput(), turnConstant);
        }

        // Stand in for a full shoot procedure
        tempShooter.shoot();
    }

    /**
     * This method will return the transform3d of the robot to the tag.
     * It checks to make sure the tag is of the correct tag ID (according to the current alliance),
     * where it will then give that transform3d.
     * This is more backend-ey, so could it go into a deeper class that we could use for abstraction?
     *
     * @return Transform3d of the robot to the tag
     * @throws AprilTagGeneralCheckedException if the tag is not found by any camera
     * @author Max Spier, 1/7/2024
     */
    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        GrayScaleCamera toUse = forwardApriltagCamera.getCamera();

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());

        int tagId = toUse.getTagIdOfBestTarget();

        // this is the tag we will be using for testing in the time being. later we will need to set
        // based on alliance color
        if (tagId == 5) {
            return robotToTag;
        }

        throw new AprilTagGeneralCheckedException("Could not find tag with the correct tagId");
    }
}
