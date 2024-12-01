package com.team766.robot.common.mechanisms;

import static com.team766.math.Math.normalizeAngleDegrees;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_DRIVE_BACK_LEFT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_DRIVE_BACK_RIGHT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_DRIVE_FRONT_LEFT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_DRIVE_FRONT_RIGHT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_GYRO;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_STEER_BACK_LEFT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_STEER_BACK_RIGHT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_STEER_FRONT_LEFT;
import static com.team766.robot.common.constants.ConfigConstants.DRIVE_STEER_FRONT_RIGHT;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.sim.Pigeon2SimState;
import com.team766.controllers.PIDController;
import com.team766.framework.Mechanism;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.PigeonGyro;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.odometry.KalmanFilter;
import com.team766.odometry.Odometry;
import com.team766.robot.common.SwerveConfig;
import com.team766.robot.common.constants.ConfigConstants;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.common.mechanisms.simulation.QuadSwerveSim;
import com.team766.simulator.Parameters;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveDrive extends Mechanism {

    private final SwerveConfig config;

    private final QuadSwerveSim sim;

    private final Pigeon2SimState gyroSimState;

    // SwerveModules
    private final SwerveModule swerveFR;
    private final SwerveModule swerveFL;
    private final SwerveModule swerveBR;
    private final SwerveModule swerveBL;

    private SwerveModuleState[] swerveModuleStates;

    private final GyroReader gyro;
    private Optional<Alliance> alliance = DriverStation.getAlliance();

    // declaration of odometry object
    private Odometry swerveOdometry;
    // variable representing current position
    private KalmanFilter kalmanFilter;

    private Translation2d[] wheelPositions;
    private SwerveDriveKinematics swerveDriveKinematics;

    private StructArrayPublisher<SwerveModuleState> swerveModuleStatePublisher =
            NetworkTableInstance.getDefault()
                    .getStructArrayTopic("SwerveStates", SwerveModuleState.struct)
                    .publish();
    
    private final StructPublisher<Pose2d> simPosePublisher =
            NetworkTableInstance.getDefault().getStructTopic("SimRobotPose", Pose2d.struct).publish();

    private final StructPublisher<Pose2d> odometryPosePublisher =
            NetworkTableInstance.getDefault().getStructTopic("OdometryRobotPose", Pose2d.struct).publish();

    private PIDController rotationPID;

    private boolean movingToTarget = false;
    private double x;
    private double y;

    private double simPrevTime;
    private TreeMap<Double, Pose2d> simPrevPoses;
    private double simPrevYaw;

    private Field2d m_field;

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

        gyroSimState = ((PigeonGyro) gyro).getCTRE().getSimState();

        rotationPID = PIDController.loadFromConfig(ConfigConstants.DRIVE_TARGET_ROTATION_PID);

        SwerveModule[] moduleList = new SwerveModule[] {swerveFR, swerveFL, swerveBR, swerveBL};
        double halfDistanceBetweenWheels = config.distanceBetweenWheels() / 2;
        this.wheelPositions =
                new Translation2d[] {
                    getPositionForWheel(config.frontRightLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.frontLeftLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.backRightLocation(), halfDistanceBetweenWheels),
                    getPositionForWheel(config.backLeftLocation(), halfDistanceBetweenWheels)
                };

        swerveDriveKinematics = new SwerveDriveKinematics(wheelPositions);

        swerveOdometry =
                new Odometry(
                        gyro,
                        moduleList,
                        config.wheelCircumference(),
                        config.driveGearRatio(),
                        config.encoderToRevolutionConstant());

        sim = new QuadSwerveSim(
                halfDistanceBetweenWheels * 2,
                halfDistanceBetweenWheels * 2,
                Parameters.ROBOT_MASS,
                Parameters.ROBOT_MOMENT_OF_INERTIA,
                List.of(
                        swerveFL.getSim(),
                        swerveFR.getSim(),
                        swerveBL.getSim(),
                        swerveBR.getSim()));
        simPrevTime = RobotProvider.instance.getClock().getTime();
        simPrevPoses = new TreeMap<>();
        m_field = new Field2d();
        SmartDashboard.putData("Field", m_field);
        kalmanFilter = new KalmanFilter();
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

        SmartDashboard.putString("Swerve Commands", "x: " + x + ", y: " + y + ", turn: " + turn);
        double yawRad =
                Math.toRadians(
                        getHeading()
                                + (alliance.isPresent() && alliance.get() == Alliance.Blue
                                        ? 0
                                        : 180));
        SmartDashboard.putNumber("rotate by: ", Math.toDegrees(yawRad));
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
        alliance = DriverStation.getAlliance();
        resetGyro(alliance.isPresent() && alliance.get().equals(Alliance.Blue) ? 0 : 180);
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
        SmartDashboard.putNumber("filtered X value", kalmanFilter.getPos().getX());
        return new Pose2d(kalmanFilter.getPos(), Rotation2d.fromDegrees(getHeading()));
    }

    public void setCurrentPosition(Pose2d P) {
        kalmanFilter.setPos(P.getTranslation());
        // log("setCurrentPosition(): " + P);
    }

    public void resetCurrentPosition() {
        kalmanFilter.setPos(new Translation2d());
        // swerveOdometry.setCurrentPosition(new Pose2d());
    }

    /**
     * @return robot relative chassis speeds
    */
    public ChassisSpeeds getRelativeChassisSpeeds() {
        return swerveDriveKinematics.toChassisSpeeds(
                swerveFR.getModuleState(),
                swerveFL.getModuleState(),
                swerveBR.getModuleState(),
                swerveBL.getModuleState());
    }
    
    /**
     * @return field relative robot velocity
     */
    public Translation2d getAbsoluteRobotVelocity() {
        ChassisSpeeds relSpeeds = getRelativeChassisSpeeds();
        return new Translation2d(relSpeeds.vxMetersPerSecond, relSpeeds.vyMetersPerSecond).rotateBy(Rotation2d.fromDegrees(getHeading()));
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
        kalmanFilter.addVelocityInput(getAbsoluteRobotVelocity(), RobotProvider.instance.getClock().getTime());
        
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

        swerveModuleStates = new SwerveModuleState[] {
                    swerveFR.getModuleState(),
                    swerveFL.getModuleState(),
                    swerveBR.getModuleState(),
                    swerveBL.getModuleState(),
                };
        if (Logger.isLoggingToDataLog()) {
            org.littletonrobotics.junction.Logger.recordOutput("curPose", getCurrentPosition());
            org.littletonrobotics.junction.Logger.recordOutput(
                    "current rotational velocity", getRelativeChassisSpeeds().omegaRadiansPerSecond);
            org.littletonrobotics.junction.Logger.recordOutput("SwerveStates", swerveModuleStates);
        }
        
        swerveModuleStatePublisher.set(swerveModuleStates);
        runSim();
    }

    public void runSim() {
        swerveFR.runSim();
        swerveFL.runSim();
        swerveBR.runSim();
        swerveBL.runSim();

        final double now = RobotProvider.instance.getClock().getTime();
        final double dt = now - simPrevTime;
        simPrevTime = now;

        sim.update(dt);

        final Pose2d pose = sim.getCurPose();

        simPrevPoses.put(now, pose);
        if(now - simPrevPoses.firstKey() > 1) {
            simPrevPoses.remove(simPrevPoses.firstKey()); // delete old values
        } 

        kalmanFilter.updateWithOdometry(swerveOdometry.predictCurrentPositionChange(), now - dt, now);

        if (Math.random() < 0.03) {
            double delay = Math.random() * 0.5;
            Pose2d prevPose = simPrevPoses.ceilingEntry(now - delay).getValue();
            double randX = prevPose.getX() + 0.2 * (Math.random() - 0.5);
            double randY = prevPose.getY() + 0.2 * (Math.random() - 0.5);
            kalmanFilter.updateWithVisionMeasurement(new Translation2d(randX, randY), now - delay /* - 0.1 * Math.random() */);
            SmartDashboard.putNumber("sensor X measurement", randX);
        }

        SmartDashboard.putNumber("true X value", pose.getX());

        simPosePublisher.set(pose);

        odometryPosePublisher.set(getCurrentPosition());

        final double yaw = pose.getRotation().getDegrees();
        gyroSimState.addYaw(normalizeAngleDegrees(yaw - simPrevYaw));
        simPrevYaw = yaw;
        SmartDashboard.putNumber("sim yaw", yaw);
        m_field.setRobotPose(pose);
    }
}
