package com.team766.robot.swerveandshoot.mechanisms;

import com.team766.ViSIONbase.*;
import com.team766.controllers.PIDController;
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

    public static ScoringPosition makerSpace1L, makerSpace1R;

    private PIDController xPID;
    private PIDController yPID;

    // Last X/Y values just in case the tag is ever lost when moving.
    private double lastX = 0;
    private double lastY = 0;

    private int tagId;

    public SpeakerShooterPowerCalculator() throws AprilTagGeneralCheckedException {
        // Instantiating PID Controllers for drive
        // These PID controllers do a good job but overshoot on longer distances (on the swerve and
        // shoot bot) but then recover nicley. I think I like them.

        xPID = new PIDController(0.40, 0.0, 0, 0, -0.75, 0.75, 0.02);
        yPID = new PIDController(0.18, 0.0, 0, 0, -0.75, 0.75, 0.02);

        // Sample positions for later
        leftPosition = new ScoringPosition(0, 0, 0, 0, 0);
        centerPosition = new ScoringPosition(0, 0, 0, 0, 0);
        rightPosition = new ScoringPosition(0, 0, 0, 0, 0);

        // Real positions that we are using rigt now
        makerSpace1L = new ScoringPosition(0, 0, 2.88, -0.92, 180);
        makerSpace1R = new ScoringPosition(0, 0, 2.9, 0.80, 180);

        // When do we know the alliance? Is that during the constructor or after?
        Optional<Alliance> currentAlliance = DriverStation.getAlliance();

        // This next code gets the alliance and sets the tag that we should be looking for to that
        // of the correct alliance according to the field layout.
        // See the AprilTag field layout on the Game Manual for the 2024 Crescendo season for more
        // information
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
            tagId = 5;
            throw new AprilTagGeneralCheckedException(
                    "Alliance not found correctly, optional is empty.");
        }

        // Setting the tag ID to 5 permanatly because this is what we are using in the Maker Space.
        // This should be changed later.
        tagId = 5;
    }

    /**
     * This method will shoot the ball into the speaker.
     * It will move the robot to the closest scoring position, where it will then shoot.
     * This method only uses the three default scoring positions, not the two that we have actually set up in the maker space.
     *
     * @throws AprilTagGeneralCheckedException previous exceptions that could have arisen from any abstracted method calls.
     * @author Max Spier, 1/7/2024
     */
    public void shootClosest() throws AprilTagGeneralCheckedException {
        goToAndScore(closestTo());
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
        GrayScaleCamera toUse = Robot.forwardApriltagCamera.getCamera();

        Transform3d robotToTag = toUse.getBestTargetTransform3d(toUse.getBestTrackedTarget());

        return robotToTag;
    }

    /*
     * This is the main method where the robot actually moves to the scoring position.
     * It sets the PID Controllers setpoints as those of the scoring location, and gets a transform 3d of the robots distance to the tag.
     * It then calculates the speed with the PID controllers using the robots current location.
     * It also checks to see if the robot is flush with the target enough, and if it isn't, it moves it so it is.
     *
     */
    public void goToAndScore(ScoringPosition score) throws AprilTagGeneralCheckedException {
        yPID.setSetpoint(score.y_position);
        xPID.setSetpoint(score.x_position);
        Transform3d robotToTag;
        double turnConstant = 0;

        try {
            robotToTag = this.getTransform3dOfRobotToTag();

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

            yPID.calculate(lastY);
            xPID.calculate(lastX);

            turnConstant = 0; // needed?
        }

        Robot.tempShooter.setAngle(score.angle);
        Robot.tempShooter.runMotors(score.power);

        Robot.drive.controlRobotOriented(yPID.getOutput(), -xPID.getOutput(), turnConstant);

        if (xPID.getOutput() + yPID.getOutput() == 0) {
            Robot.tempShooter.shoot();
        }
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
