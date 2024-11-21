package com.team766.robot.rookie_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class intake extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;
    
public intake() {
    leftMotor = RobotProvider.instance.getMotor("intake.leftMotor");
    rightMotor = RobotProvider.instance.getMotor("intake.rightMotor");
}

public void setintakePower(double leftPower, double rightPower) {
    checkContextOwnership();
    leftMotor.set(leftPower);
    rightMotor.set(rightPower);
}

} 
