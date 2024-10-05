package com.team766.robot.rookie_bot.mechanisms;

import com.team766.hal.RobotProvider;

import com.team766.hal.MotorController;

import com.team766.framework.Mechanism;

import com.team766.framework.Mechanism;

public class Drive extends Mechanism{
    private MotorController leftMotor;
    private MotorController rightMotor;

    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.Left");
        rightMotor = RobotProvider.instance.getMotor("drive.Right");
    }

    public void setMotorSpeed(double leftPower, double rightPower) {
        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }
}
