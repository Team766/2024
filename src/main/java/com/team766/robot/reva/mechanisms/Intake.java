package com.team766.robot.reva.mechanisms;

import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Intake extends Mechanism {

    public enum State {
        IN,
        OUT,
        STOPPED
    }

    private MotorController intakeMotor;
    private State state = State.STOPPED;

    public Intake() {
        intakeMotor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
    }

    public State getState() {
        return state;
    }

    public void intakeIn() {
        checkContextOwnership();
        intakeMotor.set(1);
        state = State.IN;
    }

    public void intakeOut() {
        checkContextOwnership();
        intakeMotor.set(-1);
        state = State.OUT;
    }

    public void intakeStop() {
        checkContextOwnership();
        intakeMotor.set(0);
        state = State.STOPPED;
    }
}
