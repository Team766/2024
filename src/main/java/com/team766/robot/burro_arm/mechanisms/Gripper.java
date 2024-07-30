package com.team766.robot.burro_arm.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Gripper extends Mechanism {
    private static final double INTAKE_POWER = -1.0;
    private static final double OUTTAKE_POWER = 0.5;
    private static final double IDLE_POWER = -0.1;

    private MotorController leftMotor;
    private MotorController rightMotor;

    public Gripper() {
        leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
    }

    public void setMotorPower(final double power) {
        checkContextOwnership();

        leftMotor.set(power);
        rightMotor.set(power);
    }

    public void intake() {
        setMotorPower(INTAKE_POWER);
    }

    public void outtake() {
        setMotorPower(OUTTAKE_POWER);
    }

    public void idle() {
        setMotorPower(IDLE_POWER);
    }
}
