package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.playingwithfusion.TimeOfFlight;
import com.playingwithfusion.TimeOfFlight.RangingMode;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.ValueProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake extends Mechanism {

    public enum State {
        IN,
        OUT,
        STOPPED
    }

    private record IntakePosition(double intakePower, double proximityValue) {}

    IntakePosition[] positions =
            new IntakePosition[] {
                new IntakePosition(0, 150),
                new IntakePosition(0.2, 200),
                new IntakePosition(0.4, 400),
                new IntakePosition(1.0, 480)
            };

    private static final double DEFAULT_POWER = 1.0;
    private static final double NUDGE_INCREMENT = 0.05;
    private static final double CURRENT_LIMIT = 30.0; // a little lower than max efficiency
    private static final double MAX_POWER = 1.0;
    private static final double MIN_POWER = -1 * MAX_POWER;
    private static final double IS_CLOSE_THRESHOLD = 350;

    // This should be the amount that getRange() should return less than for a note to be classified
    // as in
    private static ValueProvider<Double> threshold =
            ConfigFileReader.getInstance()
                    .getDouble("RightProximitySensor.threshold"); // needs calibration

    private MotorController intakeMotor;
    private double intakePower = DEFAULT_POWER;
    private State state = State.STOPPED;
    private TimeOfFlight sensor;

    public Intake() {
        intakeMotor = RobotProvider.instance.getMotor(INTAKE_MOTOR);
        intakeMotor.setNeutralMode(NeutralMode.Brake);
        intakeMotor.setCurrentLimit(CURRENT_LIMIT);
        sensor = new TimeOfFlight(0); // needs calibration

        sensor.setRangingMode(RangingMode.Short, 24);
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
        // SmartDashboard.putString("[INTAKE]", state.toString());
        // SmartDashboard.putNumber("[INTAKE POWER]", intakePower);
        // SmartDashboard.putNumber("[INTAKE] Current", MotorUtil.getCurrentUsage(intakeMotor));
        SmartDashboard.putNumber("Prox Sensor", sensor.getRange());
    }

    // feel free to refactor these two functions later - I didn't want to mess up existing code

    private boolean isNoteReady() {
        return (threshold.get()) > sensor.getRange() && sensor.isRangeValid();
    }

    public boolean isNoteClose() {
        return (IS_CLOSE_THRESHOLD) > sensor.getRange() && sensor.isRangeValid();
    }

    public boolean hasNoteInIntake() {
        // debug
        // log("Sensor thingy: " + sensor.getRange());
        return isNoteReady();
    }

    public void setIntakePowerForSensorDistance() {
        checkContextOwnership();
        intakePower =
                com.team766.math.Math.interpolate(
                        positions,
                        sensor.getRange(),
                        IntakePosition::proximityValue,
                        IntakePosition::intakePower);
        runIntake();
    }
}
