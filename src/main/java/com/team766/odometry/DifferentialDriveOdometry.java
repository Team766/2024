package com.team766.odometry;

import com.team766.hal.MotorController;
import com.team766.library.RateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class DifferentialDriveOdometry {

    private static final double RATE_LIMITER_TIME = 0.05;

    private RateLimiter odometryLimiter;
    private MotorController leftMotor, rightMotor;

    private Pose2d currentPosition;

    private double prevLeftEncoderValue, prevRightEncoderValue;
    private double currLeftEncoderValue, currRightEncoderValue;

    // Physical properties of the robot
    private double wheelCircumference;
    private double gearRatio;
    private double encoderToRevolutionConstant;
    private double wheelBaseWidth; // Distance between the left and right wheels (in meters)

    /**
     * Constructor for DifferentialDriveOdometry.
     *
     * @param leftMotor The motor controlling the left wheel.
     * @param rightMotor The motor controlling the right wheel.
     * @param wheelCircumference The circumference of the wheels, including treads (in meters).
     * @param gearRatio The gear ratio of the wheels.
     * @param encoderToRevolutionConstant The encoder to revolution constant of the wheels.
     * @param wheelBaseWidth The distance between the left and right wheels (in meters).
     */
    public DifferentialDriveOdometry(
            MotorController leftMotor,
            MotorController rightMotor,
            double wheelCircumference,
            double gearRatio,
            double encoderToRevolutionConstant,
            double wheelBaseWidth) {

        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
        this.wheelCircumference = wheelCircumference;
        this.gearRatio = gearRatio;
        this.encoderToRevolutionConstant = encoderToRevolutionConstant;
        this.wheelBaseWidth = wheelBaseWidth;

        odometryLimiter = new RateLimiter(RATE_LIMITER_TIME);

        currentPosition = new Pose2d(0, 0, new Rotation2d()); // Start at the origin
        prevLeftEncoderValue = 0;
        prevRightEncoderValue = 0;
        currLeftEncoderValue = 0;
        currRightEncoderValue = 0;
    }

    /**
     * Sets the current position of the robot to a specified Pose2d.
     *
     * @param position The position to set the robot to.
     */
    public void setCurrentPosition(Pose2d position) {
        currentPosition = position;
    }

    /**
     * Updates the encoder values for both the left and right wheels.
     */
    private void setCurrentEncoderValues() {
        currLeftEncoderValue = leftMotor.getSensorPosition();
        currRightEncoderValue = rightMotor.getSensorPosition();

        prevLeftEncoderValue = currLeftEncoderValue;
        prevRightEncoderValue = currRightEncoderValue;
    }

    /**
     * Calculates the robot's position and heading based on dead reckoning.
     */
    private void updatePosition() {
        // Get the change in encoder values (how much each wheel moved)
        double deltaLeft =
                (currLeftEncoderValue - prevLeftEncoderValue)
                        * (wheelCircumference / (gearRatio * encoderToRevolutionConstant));
        double deltaRight =
                (currRightEncoderValue - prevRightEncoderValue)
                        * (wheelCircumference / (gearRatio * encoderToRevolutionConstant));

        // Calculate the distance the robot traveled and the rotation change
        double distanceTraveled = (deltaLeft + deltaRight) / 2;
        double rotationChange = (deltaRight - deltaLeft) / wheelBaseWidth;

        // Get the current heading of the robot (in radians)
        double currentHeading = currentPosition.getRotation().getRadians();

        // Update the robot's position using trigonometry
        double deltaX = distanceTraveled * Math.cos(currentHeading + rotationChange / 2);
        double deltaY = distanceTraveled * Math.sin(currentHeading + rotationChange / 2);

        // Update the current position and heading of the robot
        currentPosition =
                new Pose2d(
                        currentPosition.getX() + deltaX,
                        currentPosition.getY() + deltaY,
                        currentPosition.getRotation().plus(Rotation2d.fromRadians(rotationChange)));
    }

    /**
     * Main odometry update loop. Should be called periodically to update the robot's position.
     */
    public void run() {
        if (odometryLimiter.next()) {
            setCurrentEncoderValues();
            updatePosition();
        }
    }

    /**
     * Returns the current position of the robot.
     *
     * @return The current Pose2d of the robot.
     */
    public Pose2d getCurrentPosition() {
        return currentPosition;
    }
}
