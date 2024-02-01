package com.team766.robot.akbear.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.simulator.elements.MotorController;

public class Drive extends Mechanism {

    private final MotorController leftFrontMotor;
    private final MotorController leftRearMotor;
    private final MotorController rightFrontMotor;
    private final MotorController rightRearMotor;

    public Drive() {
        // TODO: initialize these
        this.leftFrontMotor = null;
        this.leftRearMotor = null;
        this.rightFrontMotor = null;
        this.rightRearMotor = null;
    }

    public void drive(int leftPower, int rightPower) {
        // TODO: implement this
    }
}
