package com.team766.hal.mock;

import com.team766.hal.JoystickReader;
import com.team766.math.Math;
import java.util.HashMap;
import java.util.Map;

public class MockJoystick implements JoystickReader {

    private double[] axisValues;
    private boolean[] buttonValues;
    private boolean[] prevButtonValues;
    private int povValue;

    private final Map<Integer, Double> axisDeadzoneMap = new HashMap<>();
    private double defaultAxisDeadzone = 0.0;

    public MockJoystick() {
        axisValues = new double[12];
        buttonValues = new boolean[20];
        prevButtonValues = new boolean[20];
    }

    @Override
    public double getAxis(final int axis) {
        return Math.deadzone(
                axisValues[axis], axisDeadzoneMap.getOrDefault(axis, defaultAxisDeadzone));
    }

    @Override
    public boolean isAxisMoved(int axis) {
        return getAxis(axis) >= axisDeadzoneMap.getOrDefault(axis, defaultAxisDeadzone);
    }

    @Override
    public void setAxisDeadzone(int axis, double deadzone) {
        axisDeadzoneMap.put(axis, deadzone);
    }

    @Override
    public void setAllAxisDeadzone(double deadzone) {
        axisDeadzoneMap.clear();
        defaultAxisDeadzone = deadzone;
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

    public void setButton(final int button, final boolean val) {
        // Button indexes begin at 1 in WPILib, so match that here
        prevButtonValues[button - 1] = buttonValues[button - 1];
        buttonValues[button - 1] = val;
    }

    @Override
    public int getPOV() {
        return povValue;
    }

    public void setPOV(final int value) {
        povValue = value;
    }

    @Override
    public boolean getButtonPressed(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return buttonValues[button - 1] && !prevButtonValues[button - 1];
    }

    @Override
    public boolean getButtonReleased(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return !buttonValues[button - 1] && prevButtonValues[button - 1];
    }
}
