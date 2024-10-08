package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;

import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.odometry.Odometry;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.constants.ConfigConstants;
import com.team766.robot.common.constants.ControlConstants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class SwerveDrive extends Mechanism {

    private final SwerveConfig config;

    // SwerveModules
    private final SwerveModule swerveFR;
    private final SwerveModule swerveFL;
    private final SwerveModule swerveBR;
    private final SwerveModule swerveBL;

    private final GyroReader gyro;
    private Optional<Alliance> alliance = DriverStation.getAlliance();

    // declaration of odometry object
    private Odometry swerveOdometry;
    // variable representing current position

    private Translation2d[] wheelPositions;
    private SwerveDriveKinematics swerveDriveKinematics;

    private StructArrayPublisher<SwerveModuleState> swerveModuleStatePublisher =
            NetworkTableInstance.getDefault()
                    .getStructArrayTopic("SwerveStates", SwerveModuleState.struct)
                    .publish();

    private PIDController rotationPID;

    private boolean movingToTarget = false;
    private double x;
    private double y;

    public SwerveDrive(SwerveConfig config) {
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
        swerveFR =
                new SwerveModule(
                        "FR",
                        driveFR,
                        steerFR,
                        encoderFR,
                        config.driveMotorCurrentLimit(),
                        config.steerMotorCurrentLimit());
        swerveFL =
                new SwerveModule(
                        "FL",
                        driveFL,
                        steerFL,
                        encoderFL,
                        config.driveMotorCurrentLimit(),
                        config.steerMotorCurrentLimit());
        swerveBR =
                new SwerveModule(
                        "BR",
                        driveBR,
                        steerBR,
                        encoderBR,
                        config.driveMotorCurrentLimit(),
                        config.steerMotorCurrentLimit());
        swerveBL =
                new SwerveModule(
                        "BL",
                        driveBL,
                        steerBL,
                        encoderBL,
                        config.driveMotorCurrentLimit(),
                        config.steerMotorCurrentLimit());

        // Sets up odometry
        gyro = RobotProvider.instance.getGyro(DRIVE_GYRO);

        rotationPID = PIDController.loadFromConfig(ConfigConstants.DRIVE_TARGET_ROTATION_PID);

        MotorController[] motorList = new MotorController[] {driveFR, driveFL, driveBR, driveBL};
        CANcoder[] encoderList = new CANcoder[] {encoderFR, encoderFL, encoderBR, encoderBL};
        double halfDistanceBetweenWheels = config.distanceBetweenWheels() / 2;
        this.wheelPositions =
                new Translation2d[] {
                    getPositionForWheel(config.frontRightLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.frontLeftLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.backRightLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.backLeftLocation(), halfDistanceBetweenWheels)
                };

        swerveDriveKinematics = new SwerveDriveKinematics(wheelPositions);

        log("MotorList Length: " + motorList.length);
        log("CANCoderList Length: " + encoderList.length);
        swerveOdometry =
                new Odometry(
                        gyro,
                        motorList,
                        encoderList,
                        wheelPositions,
                        config.wheelCircumference(),
                        config.driveGearRatio(),
                        config.encoderToRevolutionConstant());
    }

    /**
     * Helper method to create a new vector counterclockwise orthogonal to the given one
     * @param vector input vector
     * @return clockwise orthoginal output vector
     */
    private static Vector2D createOrthogonalVector(Vector2D vector) {
        return new Vector2D(-vector.getY(), vector.getX());
    }

    /**
     * Maps parameters to robot oriented swerve movement
     * @param x the x value for the position joystick, positive being forward
     * @param y the y value for the position joystick, positive being left
     * @param turn the turn value from the rotation joystick, positive being CCW
     */
    public void controlRobotOriented(double x, double y, double turn) {
        checkContextOwnership();
        // SmartDashboard.putString(
        //         "[" + "joystick" + "]" + "x, y", String.format("%.2f, %.2f", x, y));

        // Calculate the necessary turn velocity (m/s) for each motor:
        double turnVelocity = config.wheelDistanceFromCenter() * turn;

        // Finds the vectors for turning and for translation of each module, and adds them
        // Applies this for each module
        swerveFR.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turnVelocity,
                                createOrthogonalVector(config.frontRightLocation()).normalize()));
        swerveFL.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turnVelocity,
                                createOrthogonalVector(config.frontLeftLocation()).normalize()));
        swerveBR.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turnVelocity,
                                createOrthogonalVector(config.backRightLocation()).normalize()));
        swerveBL.driveAndSteer(
                new Vector2D(x, y)
                        .add(
                                turnVelocity,
                                createOrthogonalVector(config.backLeftLocation()).normalize()));
    }

    /**
     * Uses controlRobotOriented() to control the robot relative to the field
     * @param x the x value for the position joystick, positive being forward, in meters/sec
     * @param y the y value for the position joystick, positive being left, in meters/sec
     * @param turn the turn value from the rotation joystick, positive being CCW, in radians/sec
     */
    private void controlFieldOrientedBase(double x, double y, double turn) {
        checkContextOwnership();

        double yawRad =
                Math.toRadians(
                        getHeading()
                                + (alliance.isPresent() && alliance.get() == Alliance.Blue
                                        ? 0
                                        : 180));
        // Applies a rotational translation to controlRobotOriented
        // Counteracts the forward direction changing when the robot turns
        // TODO: change to inverse rotation matrix (rather than negative angle)
        controlRobotOriented(
                Math.cos(-yawRad) * x - Math.sin(-yawRad) * y,
                Math.sin(-yawRad) * x + Math.cos(-yawRad) * y,
                turn);
    }

    /**
     * Uses controlRobotOriented() to control the robot relative to the field
     * Sets robot to manual control mode rather than a rotation setpoint
     * @param x the x value for the position joystick, positive being forward, in meters/sec
     * @param y the y value for the position joystick, positive being left, in meters/sec
     * @param turn the turn value from the rotation joystick, positive being CCW, in radians/sec
     */
    public void controlFieldOriented(double x, double y, double turn) {
        movingToTarget = false;
        controlFieldOrientedBase(x, y, turn);
    }

    /**
     * Allows for field oriented control of the robot's position while moving to a specific angle for rotation
     * @param x the x value for the position joystick, positive being forward
     * @param y the y value for the position joystick, positive being left
     * @param target rotational target as a Rotation2d, can input a null value
     */
    public void controlFieldOrientedWithRotationTarget(double x, double y, Rotation2d target) {
        checkContextOwnership();
        if (target != null) {
            rotationPID.setSetpoint(target.getDegrees());
            // SmartDashboard.putNumber("Rotation Target", target.getDegrees());
        }

        movingToTarget = true;
        this.x = x;
        this.y = y;

        // controlFieldOriented(
        //         x,
        //         y,
        //         (Math.abs(rotationPID.getOutput()) < ControlConstants.DEFAULT_ROTATION_THRESHOLD
        //                 ? 0
        //                 : rotationPID.getOutput()));
    }

    public boolean isAtRotationTarget() {
        boolean value =
                Math.abs(rotationPID.getOutput()) < ControlConstants.DEFAULT_ROTATION_THRESHOLD;
        // SmartDashboard.putBoolean("Is At Drive Rotation Target", value);
        return value;
    }

    /**
     * Overloads controlFieldOriented to work with a chassisSpeeds input
     * @param chassisSpeeds
     */
    public void controlFieldOriented(ChassisSpeeds chassisSpeeds) {
        movingToTarget = false;

        double vx = chassisSpeeds.vxMetersPerSecond;
        double vy = chassisSpeeds.vyMetersPerSecond;
        double vang = chassisSpeeds.omegaRadiansPerSecond;

        controlFieldOrientedBase(vx, vy, vang);
    }

    /**
     * Overloads controlFieldOriented to work with a chassisSpeeds input
     * @param chassisSpeeds
     */
    public void controlRobotOriented(ChassisSpeeds chassisSpeeds) {
        movingToTarget = false;

        double vx = chassisSpeeds.vxMetersPerSecond;
        double vy = chassisSpeeds.vyMetersPerSecond;
        double vang = chassisSpeeds.omegaRadiansPerSecond;

        controlRobotOriented(vx, vy, vang);
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
        swerveFR.steer(config.frontRightLocation());
        swerveFL.steer(config.frontLeftLocation());
        swerveBR.steer(config.backRightLocation());
        swerveBL.steer(config.backLeftLocation());
    }

    /**
     * Resets gyro to zero degrees relative to the driver
     * Sets to 180 degrees if the driver is on red (facing backwards)
     */
    public void resetGyro() {
        resetGyro(alliance.isPresent() && alliance.get() == Alliance.Blue ? 0 : 180);
    }

    /**
     * Sets gyro to value in degrees
     * @param angle in degrees
     */
    public void resetGyro(double angle) {
        checkContextOwnership();
        gyro.setAngle(angle);
    }

    /**
     * Gets current heading in degrees
     * @return current heading in degrees
     */
    public double getHeading() {
        return gyro.getAngle();
    }

    public double getPitch() {
        return gyro.getPitch();
    }

    public double getRoll() {
        return gyro.getRoll();
    }

    public Pose2d getCurrentPosition() {
        return swerveOdometry.getCurrPosition();
    }

    public void setCurrentPosition(Pose2d P) {
        // log("setCurrentPosition(): " + P);
        swerveOdometry.setCurrentPosition(P);
    }

    public void resetCurrentPosition() {
        swerveOdometry.setCurrentPosition(new Pose2d());
    }

    public ChassisSpeeds getChassisSpeeds() {
        return swerveDriveKinematics.toChassisSpeeds(
                swerveFR.getModuleState(),
                swerveFL.getModuleState(),
                swerveBR.getModuleState(),
                swerveBL.getModuleState());
    }

    public double maxWheelDistToCenter() {
        double max = 0;
        for (Translation2d translation : wheelPositions) {
            max = Math.max(max, translation.getNorm());
        }
        return max;
    }

    private static Translation2d getPositionForWheel(
            Vector2D relativeLocation, double halfDistance) {
        return new Translation2d(
                relativeLocation.getX() * halfDistance, relativeLocation.getY() * halfDistance);
    }

    // Odometry
    @Override
    public void run() {
        swerveOdometry.run();
        // log(currentPosition.toString());
        // SmartDashboard.putString("pos", getCurrentPosition().toString());

        // SmartDashboard.putNumber("Yaw", getHeading());
        // SmartDashboard.putNumber("Pitch", getPitch());
        // SmartDashboard.putNumber("Roll", getRoll());

        if (movingToTarget) {
            rotationPID.calculate(getHeading());
            controlFieldOrientedBase(
                    x,
                    y,
                    (Math.abs(rotationPID.getOutput()) < ControlConstants.DEFAULT_ROTATION_THRESHOLD
                            ? 0
                            : rotationPID.getOutput()));
        }

        // SmartDashboard.putBoolean("movingToTarget", movingToTarget);

        // SmartDashboard.putBoolean("isAtRotationTarget", isAtRotationTarget());

        swerveFR.dashboardCurrentUsage();
        swerveFL.dashboardCurrentUsage();
        swerveBR.dashboardCurrentUsage();
        swerveBL.dashboardCurrentUsage();

        SwerveModuleState[] states =
                new SwerveModuleState[] {
                    swerveFR.getModuleState(),
                    swerveFL.getModuleState(),
                    swerveBR.getModuleState(),
                    swerveBL.getModuleState(),
                };
        if (Logger.isLoggingToDataLog()) {
            org.littletonrobotics.junction.Logger.recordOutput("curPose", getCurrentPosition());
            org.littletonrobotics.junction.Logger.recordOutput(
                    "current rotational velocity", getChassisSpeeds().omegaRadiansPerSecond);
            org.littletonrobotics.junction.Logger.recordOutput("SwerveStates", states);
        }
        swerveModuleStatePublisher.set(states);
    }
}
