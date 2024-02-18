package com.team766.robot.reva.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Intake extends Mechanism {
    private MotorController intakeWheels;

    public Intake() {
        intakeWheels = RobotProvider.instance.getMotor(INTAKE_MOTOR);
    }

    public void intakeIn() {
        checkContextOwnership();
        intakeWheels.set(1);
    }

    public void intakeOut() {
        checkContextOwnership();
        intakeWheels.set(-1);
    }

    public void intakeStop() {
        checkContextOwnership();
        intakeWheels.set(0);
    }
}
