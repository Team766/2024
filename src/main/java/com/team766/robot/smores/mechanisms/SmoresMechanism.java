package com.team766.robot.smores.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class SmoresMechanism extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;

    public SmoresMechanism() {
        leftMotor = RobotProvider.instance.getMotor("smoresMechanism.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("smoresMechanism.rightMotor");
    }

    public void setMotorPower(final double leftPower, final double rightPower) {
        checkContextOwnership();

        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }
}
