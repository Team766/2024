package com.team766.robot.mityBites.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Drive extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;

    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
        log("left is " + leftMotor.getClass().toString());
        log("right is " + leftMotor.getClass().toString());
    }

    public void setDrivePower(double leftPower, double rightPower) {
        checkContextOwnership();
        // log("left power: " + leftPower + " rigt power: " + rightPower);
        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }

    public void OneJoystickDrive(double forward, double turn) {
        double leftMotorPower = turn + forward;
        double rightMotorPower = -turn + forward;
        setDrivePower(leftMotorPower, rightMotorPower);
    }
}
