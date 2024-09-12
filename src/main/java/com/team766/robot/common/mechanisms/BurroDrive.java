package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;
import com.team766.hal.GyroReader;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;

public class BurroDrive extends Drive {

    private final MotorController leftMotor;
    private final MotorController rightMotor;
	private final GyroReader gyro;
	private DifferentialDriveKinematics differentialDriveKinematics;
	private DifferentialDriveOdometry differentialDriveOdometry;


	private static final double DRIVE_GEAR_RATIO = ; // Gear ratio

    private static final double WHEEL_RADIUS = ; // Radius of the wheels

	private static final double MOTOR_WHEEL_FACTOR_MPS =
            1.
                    / WHEEL_RADIUS // Wheel radians/sec
                    * DRIVE_GEAR_RATIO // Motor radians/sec
                    / (2 * Math.PI); // Motor rotations/sec (what velocity mode takes));

    public BurroDrive(double trackWidthMeters) {
        loggerCategory = Category.DRIVE;

        leftMotor = RobotProvider.instance.getMotor(DRIVE_LEFT);
        rightMotor = RobotProvider.instance.getMotor(DRIVE_RIGHT);
		gyro = RobotProvider.instance.getGyro(DRIVE_GYRO);

		differentialDriveKinematics = new DifferentialDriveKinematics(trackWidthMeters);
		differentialDriveOdometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(gyro.getAngle()), leftMotor.getSensorPosition(), rightMotor.getSensorPosition());
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
		DifferentialDriveWheelSpeeds wheelSpeeds = differentialDriveKinematics.toWheelSpeeds(chassisSpeeds);
		leftMotor.set(ControlMode.Velocity, MOTOR_WHEEL_FACTOR_MPS * wheelSpeeds.leftMetersPerSecond);
		rightMotor.set(ControlMode.Velocity, MOTOR_WHEEL_FACTOR_MPS * wheelSpeeds.rightMetersPerSecond);
	}

	public Pose2d getCurrentPosition() {
		//FIXME: add code using driveOdometry
	}

	public void resetCurrentPosition() {
		//FIXME: add code using driveOdometry
	}

    /*
     * Stops each drive motor
     */
    public void stopDrive() {
        checkContextOwnership();
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }
}