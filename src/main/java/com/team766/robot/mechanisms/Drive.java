package com.team766.robot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController;
import com.team766.logging.Category;

public class Drive extends Mechanism {
	private MotorController leftMotor;
	private MotorController rightMotor;
	private EncoderReader leftEncoder;
	private EncoderReader rightEncoder;

	public Drive() {
		loggerCategory = Category.DRIVE;

		leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
		rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
		leftEncoder = RobotProvider.instance.getEncoder("drive.leftEncoder");
		rightEncoder = RobotProvider.instance.getEncoder("drive.rightEncoder");
	}

	public void setDrivePower(double leftPower, double rightPower) {
		checkContextOwnership();

		leftMotor.set(leftPower);
		rightMotor.set(rightPower);
	}

	public void setArcadeDrivePower(double forward, double turn) {
		double leftMotorPower = turn + forward;
		double rightMotorPower = -turn + forward;
		setDrivePower(leftMotorPower, rightMotorPower);
	}

	public double getEncoderDistance() {
		double leftValue = leftEncoder.getDistance();
		double rightValue = rightEncoder.getDistance();
		log("Encoders: " + leftValue + " " + rightValue);
		return (leftValue + rightValue) / 2;
	}

	public void resetEncoders() {
		checkContextOwnership();

		leftEncoder.reset();
		rightEncoder.reset();
	}
}