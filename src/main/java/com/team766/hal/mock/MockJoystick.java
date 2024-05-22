package com.team766.hal.mock;

import com.team766.framework.conditions.RulesMixin;
import com.team766.hal.JoystickReader;

public class MockJoystick implements JoystickReader {

    private final Companion companion;
    private final double[] axisValues = new double[12];
    private final boolean[] buttonValues = new boolean[20];
    private int povValue = -1;

    public MockJoystick(RulesMixin oi) {
        companion = new Companion(this, oi);
    }

    @Override
    public Companion getCompanion() {
        return companion;
    }

    @Override
    public int getMaxAxisCount() {
        return axisValues.length;
    }

    @Override
    public double getAxis(final int axis) {
        return axisValues[axis];
    }

    public void setAxis(final int axis, final double value) {
        axisValues[axis] = value;
    }

    @Override
    public int getMaxButtonCount() {
        return buttonValues.length;
    }

    @Override
    public boolean getButton(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return buttonValues[button - 1];
    }

    public void setButton(final int button, final boolean val) {
        // Button indexes begin at 1 in WPILib, so match that here
        buttonValues[button - 1] = val;
    }

    @Override
    public int getPOV() {
        return povValue;
    }

    public void setPOV(final int value) {
        povValue = value;
    }
}
