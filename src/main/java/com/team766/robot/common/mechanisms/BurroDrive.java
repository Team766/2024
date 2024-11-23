package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;

import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.wpilib.REVThroughBoreDutyCycleEncoder;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.odometry.DifferentialDriveOdometry;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
// import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;

public class BurroDrive extends Drive {

    private final MotorController leftMotor;
    private final MotorController rightMotor;
    private final REVThroughBoreDutyCycleEncoder leftEncoder;
    private final REVThroughBoreDutyCycleEncoder rightEncoder;
    private DifferentialDriveKinematics differentialDriveKinematics;
    private DifferentialDriveOdometry differentialDriveOdometry;

    // TODO set actual ratio
    private static final double DRIVE_GEAR_RATIO = 1; // Gear ratio

    // TODO set actual radius
    private static final double WHEEL_RADIUS = 1; // Radius of the wheels

    // TODO
    private static final double ENCODER_UNIT_TO_REVOLUTION_CONSTANT = 1.; 

    private static final double MOTOR_WHEEL_FACTOR_MPS =
            1.
                    / WHEEL_RADIUS // Wheel radians/sec
                    * DRIVE_GEAR_RATIO // Motor radians/sec
                    / (2 * Math.PI) // Motor rotations/sec (what velocity mode takes))
                    * ENCODER_UNIT_TO_REVOLUTION_CONSTANT; // Encoder units/sec
    
    // TODO
    private static final double TRACK_WIDTH_METERS = 0.4; // Distance between left and right wheel

    public BurroDrive() {
        loggerCategory = Category.DRIVE;

        leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");

        leftEncoder = null; //FIXME
        rightEncoder = null; //FIXME

        differentialDriveKinematics = new DifferentialDriveKinematics(TRACK_WIDTH_METERS);
        differentialDriveOdometry =
                new DifferentialDriveOdometry(
                        leftEncoder,
                        rightEncoder,
                        WHEEL_RADIUS * 2 * Math.PI,
                        DRIVE_GEAR_RATIO,
                        ENCODER_UNIT_TO_REVOLUTION_CONSTANT,
                        TRACK_WIDTH_METERS);
    }

    /**
     * @param forward how much power to apply to moving the robot (positive being forward)
     * @param turn how much power to apply to turning the robot (positive being CCW)
     */
    public void drive(double forward, double turn) {
        checkContextOwnership();
        leftMotor.set(forward - turn);
        rightMotor.set(forward + turn);
    }

    public void controlRobotOriented(ChassisSpeeds chassisSpeeds) {
        DifferentialDriveWheelSpeeds wheelSpeeds =
                differentialDriveKinematics.toWheelSpeeds(chassisSpeeds);
        leftMotor.set(
                ControlMode.Velocity, MOTOR_WHEEL_FACTOR_MPS * wheelSpeeds.leftMetersPerSecond);
        rightMotor.set(
                ControlMode.Velocity, MOTOR_WHEEL_FACTOR_MPS * wheelSpeeds.rightMetersPerSecond);
    }

    public Pose2d getCurrentPosition() {
        return differentialDriveOdometry.getCurrentPosition();
    }

    public void resetCurrentPosition() {
        differentialDriveOdometry.setCurrentPosition(new Pose2d(0, 0, new Rotation2d()));
    }

    public void setCurrentPosition(Pose2d P) {
        differentialDriveOdometry.setCurrentPosition(P);
    }

    public void resetHeading(double angle) {
        Pose2d curPose = getCurrentPosition();
        differentialDriveOdometry.setCurrentPosition(
                new Pose2d(curPose.getX(), curPose.getY(), Rotation2d.fromDegrees(angle)));
    }

    public double getHeading() {
        return getCurrentPosition().getRotation().getDegrees();
    }

    /*
     * Stops each drive motor
     */
    public void stopDrive() {
        checkContextOwnership();
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }

    public ChassisSpeeds getChassisSpeeds() {
        return differentialDriveKinematics.toChassisSpeeds(
                new DifferentialDriveWheelSpeeds(
                        leftMotor.getSensorVelocity() / MOTOR_WHEEL_FACTOR_MPS,
                        rightMotor.getSensorVelocity() / MOTOR_WHEEL_FACTOR_MPS));
    }

    public void setCross() {}

    @Override
    public void run() {
        differentialDriveOdometry.run();
    }
}
