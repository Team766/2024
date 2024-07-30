package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;

public class BurroDrive extends Mechanism {

    private final MotorController leftMotor;
    private final MotorController rightMotor;

    public BurroDrive() {
        loggerCategory = Category.DRIVE;

        leftMotor = RobotProvider.instance.getMotor(DRIVE_LEFT);
        rightMotor = RobotProvider.instance.getMotor(DRIVE_RIGHT);
    }

    /**
     * @param forward how much power to apply to moving the robot (positive being forward)
     * @param turn how much power to apply to turning the robot (positive being CCW)
     */
    public void drive(double forward, double turn) {
        checkContextOwnership();
        leftMotor.set(forward - turn);
        rightMotor.set(forward + turn);
    }

    /*
     * Stops each drive motor
     */
    public void stopDrive() {
        checkContextOwnership();
        leftMotor.stopMotor();
        rightMotor.stopMotor();
    }
}
