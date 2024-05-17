package com.team766.hal.mock;

import com.team766.framework.conditions.Condition;
import com.team766.framework.conditions.ConditionState;
import com.team766.framework.conditions.RulesMixin;
import com.team766.framework.conditions.RulesMixin.ValueCondition;
import com.team766.hal.JoystickReader;
import com.team766.library.ArrayUtils;
import com.team766.library.Lazy;

public class MockJoystick implements JoystickReader {

    private final double[] axisValues;
    private final boolean[] buttonValues;
    private int povValue;

    private final Lazy<Condition>[] buttonConditions;
    private final Lazy<ValueCondition<Double>>[] axisConditions;
    private final Lazy<ValueCondition<Integer>> povCondition;

    public MockJoystick(RulesMixin oi) {
        axisValues = new double[12];
        buttonValues = new boolean[20];
        buttonConditions = ArrayUtils.initializeArray(
                buttonValues.length,
                button -> new Lazy<>(
                        () -> oi.new DeclaredCondition(() -> this.getButtonState(button + 1))));
        axisConditions = ArrayUtils.initializeArray(
                axisValues.length,
                axis -> new Lazy<>(() -> oi.new ValueCondition<>(() -> this.getAxis(axis))));
        povCondition = new Lazy<>(() -> oi.new ValueCondition<>(() -> this.getPOV()));
    }

    @Override
    public double getAxis(final int axis) {
        return axisValues[axis];
    }

    @Override
    public ValueCondition<Double> getAxisCondition(int axis) {
        return axisConditions[axis].get();
    }

    public void setAxisValue(final int axis, final double value) {
        axisValues[axis] = value;
    }

    private boolean getButtonState(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return buttonValues[button - 1];
    }

    @Override
    public ConditionState getButton(int button) {
        return buttonConditions[button - 1].get().getState();
    }

    public void setButtonState(final int button, final boolean val) {
        // Button indexes begin at 1 in WPILib, so match that here
        buttonValues[button - 1] = val;
    }

    @Override
    public int getPOV() {
        return povValue;
    }

    @Override
    public ValueCondition<Integer> getPOVCondition() {
        return povCondition.get();
    }

    public void setPOV(final int value) {
        povValue = value;
    }
}
