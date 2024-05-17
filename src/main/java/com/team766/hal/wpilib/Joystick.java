package com.team766.hal.wpilib;

import com.team766.framework.conditions.Condition;
import com.team766.framework.conditions.ConditionState;
import com.team766.framework.conditions.RulesMixin;
import com.team766.framework.conditions.RulesMixin.ValueCondition;
import com.team766.hal.JoystickReader;
import com.team766.library.ArrayUtils;
import com.team766.library.Lazy;
import edu.wpi.first.hal.DriverStationJNI;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
    private final Lazy<Condition>[] buttonConditions;
    private final Lazy<ValueCondition<Double>>[] axisConditions;
    private final Lazy<ValueCondition<Double>> disconnectedAxis;
    private final Lazy<ValueCondition<Integer>> povCondition;

    public Joystick(RulesMixin oi, final int port) {
        super(port);
        buttonConditions = ArrayUtils.initializeArray(
                32, // Buttons are represented in the DriverStation as a 32-bit integer.
                button -> new Lazy<>(
                        () -> oi.new DeclaredCondition(() -> this.getButtonState(button + 1))));
        axisConditions = ArrayUtils.initializeArray(
                DriverStationJNI.kMaxJoystickAxes,
                axis -> new Lazy<>(() -> oi.new ValueCondition<>(() -> this.getAxis(axis))));
        disconnectedAxis = new Lazy<>(() -> oi.new ValueCondition<>(() -> 0.0));
        povCondition = new Lazy<>(() -> oi.new ValueCondition<>(() -> this.getPOV()));
    }

    @Override
    public double getAxis(final int axis) {
        return getRawAxis(axis);
    }

    @Override
    public ValueCondition<Double> getAxisCondition(int axis) {
        if (axis < axisConditions.length) {
            return axisConditions[axis].get();
        } else {
            return disconnectedAxis.get();
        }
    }

    private boolean getButtonState(final int button) {
        return getRawButton(button);
    }

    @Override
    public ConditionState getButton(int button) {
        button -= 1;
        if (button < buttonConditions.length) {
            return buttonConditions[button].get().getState();
        } else {
            return ConditionState.IsNotTriggering;
        }
    }

    @Override
    public int getPOV() {
        return super.getPOV();
    }

    @Override
    public ValueCondition<Integer> getPOVCondition() {
        return povCondition.get();
    }
}
