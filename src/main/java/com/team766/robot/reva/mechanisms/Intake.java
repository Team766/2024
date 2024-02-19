package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends Mechanism {

    public enum State {
        IN,
        OUT,
        STOPPED
    }

    private static final double DEFAULT_POWER = 0.5;
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1 * MAX_POWER;

    private MotorController intakeMotor;
    private double intakePower = DEFAULT_POWER;
    private State state = State.STOPPED;

    public Intake() {
        intakeMotor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
    }

    public State getState() {
        return state;
    }

    public void runIntake() {
        checkContextOwnership();
        intakeMotor.set(intakePower);
        if (intakePower == 0) {
            state = State.STOPPED;
        } else {
            state = intakePower > 0 ? State.IN : State.OUT;
        }
    }

    public void in() {
        intakePower = DEFAULT_POWER;
        runIntake();
    }

    public void out() {
        intakePower = -1 * DEFAULT_POWER;
        runIntake();
    }

    public void stop() {
        intakePower = 0.0;
        runIntake();
    }

    public void nudgeUp() {
        checkContextOwnership();
        intakePower = Math.min(intakePower + NUDGE_INCREMENT, MAX_POWER);
        intakeMotor.set(intakePower);
    }

    public void nudgeDown() {
        checkContextOwnership();
        intakePower = Math.max(intakePower - NUDGE_INCREMENT, MIN_POWER);
        intakeMotor.set(intakePower);
    }

    public void run() {
        SmartDashboard.putString("[INTAKE]", state.toString());
        SmartDashboard.putNumber("[INTAKE POWER]", intakePower);
    }

    // TODO: Implement

    public boolean hasNoteInIntake() {
        return false;
    }
}
;
