package com.team766.robot.common.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.ctre.phoenix.sensors.CANCoder;
import com.team766.framework.Mechanism;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.odometry.Odometry;
import com.team766.odometry.Point;
import com.team766.odometry.PointDir;
import com.team766.robot.gatorade.constants.OdometryInputConstants;
import com.team766.robot.gatorade.constants.SwerveDriveConstants;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Drive extends Mechanism {

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

    public Drive() {
        loggerCategory = Category.DRIVE;

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
        CANCoder encoderFR = new CANCoder(2, SwerveDriveConstants.SWERVE_CANBUS);
        CANCoder encoderFL = new CANCoder(4, SwerveDriveConstants.SWERVE_CANBUS);
        CANCoder encoderBR = new CANCoder(3, SwerveDriveConstants.SWERVE_CANBUS);
        CANCoder encoderBL = new CANCoder(1, SwerveDriveConstants.SWERVE_CANBUS);

        // initialize the swerve modules
        swerveFR = new SwerveModule("FR", driveFR, steerFR, encoderFR);
        swerveFL = new SwerveModule("FL", driveFL, steerFL, encoderFL);
        swerveBR = new SwerveModule("BR", driveBR, steerBR, encoderBR);
        swerveBL = new SwerveModule("BL", driveBL, steerBL, encoderBL);

        // Sets up odometry
        gyro = RobotProvider.instance.getGyro(DRIVE_GYRO);

        currentPosition = new PointDir(0, 0, 0);
        MotorController[] motorList = new MotorController[] {driveFR, driveFL, driveBL, driveBR};
        CANCoder[] encoderList = new CANCoder[] {encoderFR, encoderFL, encoderBL, encoderBR};
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

    /**
     * Maps parameters to robot oriented swerve movement
     * @param x the x value for the position joystick
     * @param y the y value for the position joystick
     * @param turn the turn value from the rotation joystick
     */
    public void controlRobotOriented(double x, double y, double turn) {
        checkContextOwnership();

        // Finds the vectors for turning and for translation of each module, and adds them
        // Applies this for each module
        swerveFL.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turn,
                                new Vector2D(SwerveDriveConstants.FL_Y, SwerveDriveConstants.FL_X)
                                        .normalize()));
        swerveFR.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turn,
                                new Vector2D(SwerveDriveConstants.FR_Y, SwerveDriveConstants.FR_X)
                                        .normalize()));
        swerveBR.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turn,
                                new Vector2D(SwerveDriveConstants.BR_Y, SwerveDriveConstants.BR_X)
                                        .normalize()));
        swerveBL.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turn,
                                new Vector2D(SwerveDriveConstants.BL_Y, SwerveDriveConstants.BL_X)
                                        .normalize()));
    }

    /**
     * Uses controlRobotOriented() to control the robot relative to the field
     * @param yawRad the robot gyro's current yaw value in radians
     * @param x the x value for the position joystick
     * @param y the y value for the position joystick
     * @param turn the turn value from the rotation joystick
     */
    public void controlFieldOriented(double yawRad, double x, double y, double turn) {
        checkContextOwnership();

        // Applies a rotational translation to controlRobotOriented
        // Counteracts the forward direction changing when the robot turns
        // TODO: change to inverse rotation matrix (rather than negative angle)
        controlRobotOriented(
                Math.cos(-yawRad) * x - Math.sin(-yawRad) * y,
                Math.sin(-yawRad) * x + Math.cos(-yawRad) * y,
                turn);
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

        swerveFL.steer(new Vector2D(SwerveDriveConstants.FL_Y, -SwerveDriveConstants.FL_X));
        swerveFR.steer(new Vector2D(SwerveDriveConstants.FR_Y, -SwerveDriveConstants.FR_X));
        swerveBL.steer(new Vector2D(SwerveDriveConstants.BL_Y, -SwerveDriveConstants.BL_X));
        swerveBR.steer(new Vector2D(SwerveDriveConstants.BR_Y, -SwerveDriveConstants.BR_X));
    }

    public void resetGyro() {
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
