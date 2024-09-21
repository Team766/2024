package com.team766.robot.rookie_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

/*
 * Motor IDs:
 * Drive_L - 2
 * Drive_R - 11
 * Intake_L - 30
 * Intake_R - 3
 * Elevator - 8
 */
public class Drive extends Mechanism {
    public MotorController leftMotor;
    public MotorController rightMotor;

    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("drive.rightMotor");
    }

    public void drive(double leftPower, double rightPower) {
        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }
    
    public void setArcadeDrivePower(double forward, double turn) {
        double leftMotorPower= 0.75*(turn - forward);
        double rightMotorPower= 0.75*(-turn - forward);
        drive(leftMotorPower, rightMotorPower);
        
        

    }
}
