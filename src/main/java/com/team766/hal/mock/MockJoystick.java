package com.team766.hal.mock;

import com.team766.hal.JoystickReader;

public class MockJoystick implements JoystickReader {

    private final double[] axisValues;
    private final boolean[] buttonValues;
    private int povValue;

    public MockJoystick() {
        axisValues = new double[12];
        buttonValues = new boolean[20];
    }

    @Override
    public double getAxis(final int axis) {
        return axisValues[axis];
    }

    @Override
    public boolean getButton(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return buttonValues[button - 1];
    }

    public void setAxisValue(final int axis, final double value) {
        axisValues[axis] = value;
    }

    public void setButtonState(final int button, final boolean val) {
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
