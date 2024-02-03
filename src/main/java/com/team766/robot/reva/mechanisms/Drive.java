package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Drive extends Mechanism {

    private final MotorController driveFR;
    private final MotorController driveFL;
    private final MotorController driveBR;
    private final MotorController driveBL;

    public Drive() {
        driveFR = RobotProvider.instance.getMotor(DRIVE_DRIVE_FRONT_RIGHT);
        driveFL = RobotProvider.instance.getMotor(DRIVE_DRIVE_FRONT_LEFT);
        driveBR = RobotProvider.instance.getMotor(DRIVE_DRIVE_BACK_RIGHT);
        driveBL = RobotProvider.instance.getMotor(DRIVE_DRIVE_BACK_LEFT);
    }

    public void drive(double leftPower, double rightPower) {
        checkContextOwnership();
        driveFL.set(leftPower);
        driveFR.set(rightPower);
        driveBL.set(leftPower);
        driveBR.set(rightPower);
    }
}
