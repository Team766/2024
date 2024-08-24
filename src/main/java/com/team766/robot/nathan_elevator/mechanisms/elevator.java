package com.team766.robot.nathan_elevator.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class elevator extends Mechanism {
    private MotorController elevatorMotor;
    final double ELEVATOR_UPPER_CONSTANT = 10;
    final double ELEVATOR_LOWER_CONSTANT = -10;
    public elevator() {
        elevatorMotor = RobotProvider.instance.getMotor("elevator.motor");
    }

    public void setMotorPower(final double elevatorPower) {
        checkContextOwnership();
        if(elevatorMotor.getSensorPosition() < ELEVATOR_UPPER_CONSTANT && elevatorMotor.getSensorPosition() > ELEVATOR_LOWER_CONSTANT) {
            elevatorMotor.set(elevatorPower);
        }
    }
}
