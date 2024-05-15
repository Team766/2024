package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;

import com.ctre.phoenix6.hardware.CANcoder;
import com.team766.controllers.PIDController;
import com.team766.framework.Subsystem;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
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
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.littletonrobotics.junction.AutoLogOutput;

public class Drive extends Subsystem<Drive.State, Drive.Goal> {
    /**
     * @param heading current heading in degrees
     */
    public static record State(
            @AutoLogOutput double heading,
            @AutoLogOutput double pitch,
            @AutoLogOutput double roll,
            @AutoLogOutput Pose2d currentPosition,
            @AutoLogOutput ChassisSpeeds chassisSpeeds,
            @AutoLogOutput SwerveModuleState[] swerveStates) {

        public boolean isAtRotationTarget(double targetHeading) {
            // TODO: this should also check that rotational velocity is low
            // TODO: this should use modular arithmetic
            return Math.abs(targetHeading - heading) < ControlConstants.AT_ROTATION_ANGLE_THRESHOLD;
        }

        public boolean isAtRotationTarget(Rotation2d targetHeading) {
            return isAtRotationTarget(targetHeading.getDegrees());
        }
    }

    public sealed interface Goal {}

    public record RobotOrientedVelocity(double x, double y, double turn) implements Goal {
        public RobotOrientedVelocity(ChassisSpeeds chassisSpeeds) {
            this(
                    chassisSpeeds.vxMetersPerSecond,
                    chassisSpeeds.vyMetersPerSecond,
                    chassisSpeeds.omegaRadiansPerSecond);
        }
    }

    public record FieldOrientedVelocity(double x, double y, double turn) implements Goal {}

    public record FieldOrientedVelocityWithRotationTarget(double x, double y, Rotation2d target)
            implements Goal {}

    public record StopDrive() implements Goal {}

    public record SetCross() implements Goal {}

    // TODO: this doesn't really seem like a goal state
    public record ResetGyro() implements Goal {}

    // TODO: this doesn't really seem like a goal state
    public record ResetCurrentPosition() implements Goal {}

    @Override
    protected void dispatch(State state, Goal goal) {
        switch (goal) {
            case RobotOrientedVelocity g -> {
                controlRobotOriented(g.x, g.y, g.turn);
            }
            case FieldOrientedVelocity g -> {
                controlFieldOriented(state, g.x, g.y, g.turn);
            }
            case FieldOrientedVelocityWithRotationTarget g -> {
                controlFieldOrientedWithRotationTarget(state, g.x, g.y, g.target);
            }
            case StopDrive s -> {
                stopDrive();
            }
            case SetCross s -> {
                stopDrive();
                setCross();
            }
            case ResetGyro r -> {
                resetGyro();
            }
            case ResetCurrentPosition r -> {
                resetCurrentPosition();
            }
        }
        ;
    }

    private final SwerveConfig config;

    // SwerveModules
    private final SwerveModule swerveFR;
    private final SwerveModule swerveFL;
    private final SwerveModule swerveBR;
    private final SwerveModule swerveBL;

    private final GyroReader gyro;

    // declaration of odometry object
    private Odometry swerveOdometry;
    // variable representing current position

    private Translation2d[] wheelPositions;
    private SwerveDriveKinematics swerveDriveKinematics;

    private PIDController rotationPID;

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
    private void controlRobotOriented(double x, double y, double turn) {
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
    private void controlFieldOriented(State state, double x, double y, double turn) {
        final Optional<Alliance> alliance = DriverStation.getAlliance();
        double yawRad =
                Math.toRadians(
                        state.heading()
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
     * Allows for field oriented control of the robot's position while moving to a specific angle for rotation
     * @param x the x value for the position joystick, positive being forward
     * @param y the y value for the position joystick, positive being left
     * @param target rotational target as a Rotation2d, can input a null value
     */
    private void controlFieldOrientedWithRotationTarget(
            State state, double x, double y, Rotation2d target) {
        if (target != null) {
            rotationPID.setSetpoint(target.getDegrees());
            // SmartDashboard.putNumber("Rotation Target", target.getDegrees());
        }

        rotationPID.calculate(state.heading());

        final boolean isAtRotationTarget = state.isAtRotationTarget(target.getDegrees());

        controlFieldOriented(state, x, y, isAtRotationTarget ? 0 : rotationPID.getOutput());

        // SmartDashboard.putBoolean("Is At Drive Rotation Target", isAtRotationTarget);
    }

    /*
     * Stops each drive motor
     */
    private void stopDrive() {
        swerveFR.stopDrive();
        swerveFL.stopDrive();
        swerveBR.stopDrive();
        swerveBL.stopDrive();
    }

    /*
     * Turns wheels in a cross formation to prevent robot from moving
     */
    private void setCross() {
        swerveFR.steer(config.frontRightLocation());
        swerveFL.steer(config.frontLeftLocation());
        swerveBR.steer(config.backRightLocation());
        swerveBL.steer(config.backLeftLocation());
    }

    /**
     * Resets gyro to zero degrees relative to the driver
     * Sets to 180 degrees if the driver is on red (facing backwards)
     */
    private void resetGyro() {
        final Optional<Alliance> alliance = DriverStation.getAlliance();
        resetGyro(alliance.isPresent() && alliance.get() == Alliance.Blue ? 0 : 180);
    }

    /**
     * Sets gyro to value in degrees
     * @param angle in degrees
     */
    private void resetGyro(double angle) {
        gyro.setAngle(angle);
    }

    private void setCurrentPosition(Pose2d P) {
        // log("setCurrentPosition(): " + P);
        swerveOdometry.setCurrentPosition(P);
    }

    private void resetCurrentPosition() {
        swerveOdometry.setCurrentPosition(new Pose2d());
    }

    private double maxWheelDistToCenter() {
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
    protected State updateState() {
        swerveOdometry.run();

        final double heading = gyro.getAngle();
        final double pitch = gyro.getPitch();
        final double roll = gyro.getRoll();
        final Pose2d currentPosition = swerveOdometry.getCurrPosition();

        final ChassisSpeeds chassisSpeeds =
                swerveDriveKinematics.toChassisSpeeds(
                        swerveFR.getModuleState(),
                        swerveFL.getModuleState(),
                        swerveBR.getModuleState(),
                        swerveBL.getModuleState());

        swerveFR.dashboardCurrentUsage();
        swerveFL.dashboardCurrentUsage();
        swerveBR.dashboardCurrentUsage();
        swerveBL.dashboardCurrentUsage();

        SwerveModuleState[] swerveStates =
                new SwerveModuleState[] {
                    swerveFR.getModuleState(),
                    swerveFL.getModuleState(),
                    swerveBR.getModuleState(),
                    swerveBL.getModuleState(),
                };

        return new State(heading, pitch, roll, currentPosition, chassisSpeeds, swerveStates);
    }
}
