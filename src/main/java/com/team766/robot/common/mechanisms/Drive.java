package com.team766.robot.common.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.framework.Mechanism;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.odometry.Odometry;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.gatorade.constants.OdometryInputConstants;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Drive extends Mechanism {

    private final SwerveConfig config;

    // SwerveModules
    private final SwerveModule swerveFR;
    private final SwerveModule swerveFL;
    private final SwerveModule swerveBR;
    private final SwerveModule swerveBL;

    // TODO: rework odometry so it doesn't have to go through drive

    private final GyroReader gyro;
    // declaration of odometry object
    private Odometry swerveOdometry;
    // variable representing current position
    private static PointDir currentPosition;

    public Drive(SwerveConfig config) {
        loggerCategory = Category.DRIVE;

        this.config = config;

        // create the drive motors
        MotorController driveFR = RobotProvider.instance.getMotor(DRIVE_DRIVE_FRONT_RIGHT);
        MotorController driveFL = RobotProvider.instance.getMotor(DRIVE_DRIVE_FRONT_LEFT);
        MotorController driveBR = RobotProvider.instance.getMotor(DRIVE_DRIVE_BACK_RIGHT);
        MotorController driveBL = RobotProvider.instance.getMotor(DRIVE_DRIVE_BACK_LEFT);

        // create the steering motors
        MotorController steerFR = RobotProvider.instance.getMotor(DRIVE_STEER_FRONT_RIGHT);
        MotorController steerFL = RobotProvider.instance.getMotor(DRIVE_STEER_FRONT_LEFT);
        MotorController steerBR = RobotProvider.instance.getMotor(DRIVE_STEER_BACK_RIGHT);
        MotorController steerBL = RobotProvider.instance.getMotor(DRIVE_STEER_BACK_LEFT);

        // create the encoders
        CANcoder encoderFR = new CANcoder(2, config.canBus());
        CANcoder encoderFL = new CANcoder(4, config.canBus());
        CANcoder encoderBR = new CANcoder(3, config.canBus());
        CANcoder encoderBL = new CANcoder(1, config.canBus());

        // initialize the swerve modules
        swerveFR = new SwerveModule("FR", driveFR, steerFR, encoderFR);
        swerveFL = new SwerveModule("FL", driveFL, steerFL, encoderFL);
        swerveBR = new SwerveModule("BR", driveBR, steerBR, encoderBR);
        swerveBL = new SwerveModule("BL", driveBL, steerBL, encoderBL);

        // Sets up odometry
        gyro = RobotProvider.instance.getGyro(DRIVE_GYRO);

        currentPosition = new PointDir(0, 0, 0);
        MotorController[] motorList = new MotorController[] {driveFR, driveFL, driveBL, driveBR};
        CANcoder[] encoderList = new CANcoder[] {encoderFR, encoderFL, encoderBL, encoderBR};
        Point[] wheelPositions =
                new Point[] {
                    new Point(
                            OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2,
                            OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
                    new Point(
                            OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2,
                            -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
                    new Point(
                            -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2,
                            -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2),
                    new Point(
                            -OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2,
                            OdometryInputConstants.DISTANCE_BETWEEN_WHEELS / 2)
                };
        log("MotorList Length: " + motorList.length);
        log("CANCoderList Length: " + encoderList.length);
        swerveOdometry =
                new Odometry(
                        gyro,
                        motorList,
                        encoderList,
                        wheelPositions,
                        OdometryInputConstants.WHEEL_CIRCUMFERENCE,
                        OdometryInputConstants.GEAR_RATIO,
                        OdometryInputConstants.ENCODER_TO_REVOLUTION_CONSTANT,
                        OdometryInputConstants.RATE_LIMITER_TIME);
    }

    private static Vector2D createOrthogonalVector(Vector2D vector) {
        return new Vector2D(vector.getY(), -vector.getX());
    }

    /**
     * Maps parameters to robot oriented swerve movement
     * @param x the x value for the position joystick
     * @param y the y value for the position joystick
     * @param turn the turn value from the rotation joystick
     */
    public void controlRobotOriented(double x, double y, double turn) {
        checkContextOwnership();
        SmartDashboard.putString(
                "[" + "joystick" + "]" + "x, y", String.format("%.2f, %.2f", x, y));

        // Finds the vectors for turning and for translation of each module, and adds them
        // Applies this for each module
        swerveFL.driveAndSteer(
                new Vector2D(x, y)
                        .add(turn, createOrthogonalVector(config.frontLeftLocation()).normalize()));
        swerveFR.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turn,
                                createOrthogonalVector(config.frontRightLocation()).normalize()));
        swerveBR.driveAndSteer(
                new Vector2D(x, y)
                        .add(turn, createOrthogonalVector(config.backRightLocation()).normalize()));
        swerveBL.driveAndSteer(
                new Vector2D(x, y)
                        .add(turn, createOrthogonalVector(config.backLeftLocation()).normalize()));
    }

    /**
     * Uses controlRobotOriented() to control the robot relative to the field
     * @param yawRad the robot gyro's current yaw value in radians
     * @param x the x value for the position joystick
     * @param y the y value for the position joystick
     * @param turn the turn value from the rotation joystick
     */
    public void controlFieldOriented(double x, double y, double turn) {
        checkContextOwnership();

        double yawRad = Math.toRadians(getHeading());
        // Applies a rotational translation to controlRobotOriented
        // Counteracts the forward direction changing when the robot turns
        // TODO: change to inverse rotation matrix (rather than negative angle)
        controlRobotOriented(
                Math.cos(-yawRad) * x - Math.sin(-yawRad) * y,
                Math.sin(-yawRad) * x + Math.cos(-yawRad) * y,
                turn);
    }

    /**
     * Overloads controlFieldOriented to work with a chassisSpeeds input
     * @param yawRad
     * @param chassisSpeeds
     */
    public void controlFieldOriented(ChassisSpeeds chassisSpeeds) {
        double vx = chassisSpeeds.vxMetersPerSecond;
        double vy = chassisSpeeds.vyMetersPerSecond;
        double vang = chassisSpeeds.omegaRadiansPerSecond;

        controlFieldOriented(vx, vy, vang);
    }

    /*
     * Stops each drive motor
     */
    public void stopDrive() {
        checkContextOwnership();
        swerveFR.stopDrive();
        swerveFL.stopDrive();
        swerveBR.stopDrive();
        swerveBL.stopDrive();
    }

    /*
     * Turns wheels in a cross formation to prevent robot from moving
     */
    public void setCross() {
        checkContextOwnership();

        // TODO: use createOrthogonalVector?
        swerveFL.steer(
                new Vector2D(
                        config.frontLeftLocation().getY(), -config.frontLeftLocation().getX()));
        swerveFR.steer(
                new Vector2D(
                        config.frontRightLocation().getY(), -config.frontRightLocation().getX()));
        swerveBL.steer(
                new Vector2D(config.backLeftLocation().getY(), -config.backLeftLocation().getX()));
        swerveBR.steer(
                new Vector2D(
                        config.backRightLocation().getY(), -config.backRightLocation().getX()));
    }

    public void resetGyro() {
        checkContextOwnership();
        gyro.reset();
    }

    public double getHeading() {
        return gyro.getAngle();
    }

    public double getPitch() {
        return gyro.getPitch();
    }

    public double getRoll() {
        return gyro.getRoll();
    }

    // TODO: rework odometry so it doesn't have to go through drive
    // TODO: figure out why odometry x and y are swapped
    public PointDir getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Point P) {
        swerveOdometry.setCurrentPosition(P);
    }

    public void resetCurrentPosition() {
        swerveOdometry.setCurrentPosition(new Point(0, 0));
    }

    // Odometry
    @Override
    public void run() {
        currentPosition = swerveOdometry.run();
        log(currentPosition.toString());
        SmartDashboard.putString("position", currentPosition.toString());

        SmartDashboard.putNumber("Yaw", getHeading());
        SmartDashboard.putNumber("Pitch", getPitch());
        SmartDashboard.putNumber("Roll", getRoll());
    }
}
