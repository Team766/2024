package com.team766.robot.burro_arm.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Gripper extends Mechanism {
    private final MotorController leftMotor;
    private final MotorController rightMotor;
    private final double intakePower = -ConfigFileReader.instance.getDouble("gripper.intakePower").valueOr(0.3);
    private final double outtakePower = ConfigFileReader.instance.getDouble("gripper.outtakePower").valueOr(0.1);
    private final double idlePower = -ConfigFileReader.instance.getDouble("gripper.idlePower").valueOr(0.05);

    public Gripper() {
        leftMotor = RobotProvider.instance.getMotor("gripper.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("gripper.rightMotor");
    }

    public void setMotorPower(final double power) {
        checkContextOwnership();

        leftMotor.set(power);
        rightMotor.set(power);
    }

    public void intake() {
        setMotorPower(intakePower);
    }

    public void outtake() {
        setMotorPower(outtakePower);
    }

    public void idle() {
        setMotorPower(idlePower);
    }
}
