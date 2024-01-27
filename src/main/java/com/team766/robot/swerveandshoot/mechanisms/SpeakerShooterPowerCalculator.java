package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.controllers.PIDController;
import com.team766.framework.AprilTagGeneralCheckedException;
import com.team766.framework.Mechanism;
import com.team766.robot.swerveandshoot.*;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.*;

/*
* This is the class where we will calculate power for the shooter when it is scoring into the
speaker.
* In order to do this, right now we will use a lookup table with values. We will use the center
apriltag.
* We will move the robot to the closest of 3 scoring positions, where it will then use the preset
scoring values to score.
* Any value set to null is a value we need to figure out
*/

public class SpeakerShooterPowerCalculator extends Mechanism {

    public static ScoringPosition leftPosition;
    public static ScoringPosition centerPosition;
    public static ScoringPosition rightPosition;

    private boolean yDone = false;

    private PIDController xPID;
    private PIDController yPID;

    private int tagId;

    public SpeakerShooterPowerCalculator() throws AprilTagGeneralCheckedException {
        // These positions need to be of robot relative to tag
        // Y [<--------->] should be first
        // X [vertical] should be second

        // new swerve code should handle this for us
        // need to find viable deadzone amounts, i say maybe 0.02meters?
        // deadzones should be included in the PID controllers so if they report 0.000 power then
        // switch

        // P I D FF OL OM TH
        xPID = new PIDController(0.2, 0, 0, 0, -0.5, 0.5, 0.02);
        yPID = new PIDController(0.2, 0, 0, 0, -0.5, 0.5, 0.02);
        leftPosition = new ScoringPosition(0, 0, 0, 0, 0);
        centerPosition = new ScoringPosition(0, 0, 0, 0, 0);
        rightPosition = new ScoringPosition(0, 0, 0, 0, 0);

        // When do we know the alliance? Is that during the constructor or after?
        Optional<Alliance> currentAlliance = DriverStation.getAlliance();

        if (currentAlliance.isPresent()) {
            if (currentAlliance.get() == Alliance.Red) {
                tagId = 4;
            } else if (currentAlliance.get() == Alliance.Blue) {
                tagId = 7;
            } else {
                throw new AprilTagGeneralCheckedException(
                        "Alliance not found correctly, neiter red nor blue somehow");
            }
        } else {
            throw new AprilTagGeneralCheckedException(
                    "Alliance not found correctly, optional is empty.");
        }
    }

    /**
     * This method will shoot the ball into the speaker.
     * It will move the robot to the closest scoring position, where it will then shoot.
     * This method is not complete, it is basically just psuedocode right now.
     * Once we have actual mechanism code (or at least a design), this can be finished.
     *
     * @throws AprilTagGeneralCheckedException previous exceptions that could have arisen from any abstracted method calls.
     * @author Max Spier, 1/7/2024
     */
    public void shoot() throws AprilTagGeneralCheckedException {
        ScoringPosition score = closestTo();
        yPID.setSetpoint(score.y_position);
        yPID.calculate(this.getTransform3dOfRobotToTag().getY());

        xPID.setSetpoint(score.x_position);
        xPID.calculate(this.getTransform3dOfRobotToTag().getX());
        if (yPID.getOutput() == 0) {
            yDone = true;
        }

        Robot.tempShooter.setAngle(score.angle);
        Robot.tempShooter.runMotors(score.power);

        Robot.drive.controlRobotOriented(xPID.getOutput(), yPID.getOutput(), 0);

        if (xPID.getOutput() + yPID.getOutput() == 0) {
            Robot.tempShooter.shoot();
        }
    }

    /**
     * This method will return the transform3d of the robot to the tag.
     * It checks to make sure the tag is of the correct tag ID (according to the current alliance),
     * where it will then give that transform3d.
     *
     * @return Transform3d of the robot to the tag
     * @throws AprilTagGeneralCheckedException if the tag is not found by any camera
     * @author Max Spier, 1/7/2024
     */
    private Transform3d getTransform3dOfRobotToTag() throws AprilTagGeneralCheckedException {
        CameraPlus toUse;
        try {
            toUse = VisionUtil.findApriltagCameraThatHas(tagId);
        } catch (AprilTagGeneralCheckedException e) {
            throw new AprilTagGeneralCheckedException("Cameras could not find tag, try again.");
        }

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());
        return robotToTag;
    }

    /**
     * This method will return the closest scoring position to the robot.
     * It will use the robotToTag transform3d to find the closest position.
     *
     * @return ScoringPosition of the closest position to the robot
     * @throws AprilTagGeneralCheckedException previous exceptions that could have arisen from any
     * abstracted method calls.
     * @author Max Spier, 1/7/2024
     */
    private ScoringPosition closestTo() throws AprilTagGeneralCheckedException {

        Transform3d robotToTag = this.getTransform3dOfRobotToTag();

        double x = robotToTag.getX();
        double y = robotToTag.getY();

        // destroy transform3d to save memory

        double left =
                Math.sqrt(
                        Math.pow(x - leftPosition.x_position, 2)
                                + Math.pow(y - leftPosition.y_position, 2));
        double center =
                Math.sqrt(
                        Math.pow(x - centerPosition.x_position, 2)
                                + Math.pow(y - centerPosition.y_position, 2));
        double right =
                Math.sqrt(
                        Math.pow(x - rightPosition.x_position, 2)
                                + Math.pow(y - rightPosition.y_position, 2));

        double minValue = Math.min(Math.min(left, center), right);

        if (minValue == center) {
            return centerPosition;
        } else if (minValue == left) {
            return leftPosition;
        } else {
            return rightPosition;
        }
    }
}
