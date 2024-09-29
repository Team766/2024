package com.team766.robot.rookie_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Drive extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;
    public Drive() {
        leftMotor = RobotProvider.instance.getMotor("drive.Left");
        rightMotor = RobotProvider.instance.getMotor("drive.Right");
    }
    public void go(double leftPower, double rightPower) {
        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }

    public void arcadeDrive(double forward, double turn) {
        leftMotor.set(turn + forward);
        rightMotor.set(forward - turn);
    }
}
