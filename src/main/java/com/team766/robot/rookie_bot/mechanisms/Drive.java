package com.team766.robot.rookie_bot.mechanisms;

import com.team766.framework.Mechanism;

import com.team766.hal.RobotProvider;

import com.team766.framework.Mechanism;

import com.team766.hal.MotorController;

import com.team766.hal.MotorController;

import com.team766.framework.Mechanism;

import com.team766.hal.RobotProvider;

import com.team766.hal.MotorController;

public class Drive extends Mechanism {
private MotorController LeftMotor;
private MotorController RightMotor;
    
 public Drive() {
    LeftMotor = RobotProvider.instance.getMotor("drive.Left");
    RightMotor = RobotProvider.instance.getMotor("drive.Right");
}

public void setMotorSpeed(double leftMotorPower, double rightMotorPower) {
LeftMotor.set(leftMotorPower);
RightMotor.set(rightMotorPower);
}
}
