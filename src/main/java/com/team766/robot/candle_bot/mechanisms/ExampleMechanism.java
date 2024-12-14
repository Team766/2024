package com.team766.robot.candle_bot.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class ExampleMechanism extends Mechanism {
    private MotorController leftMotor;
    private MotorController rightMotor;

    public ExampleMechanism() {
        leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
    }

    public void setMotorPower(final double leftPower, final double rightPower) {
        checkContextOwnership();

        leftMotor.set(leftPower);
        rightMotor.set(rightPower);
    }
}

