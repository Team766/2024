package com.team766.hal.simulator;

import com.team766.framework.conditions.RulesMixin;
import com.team766.hal.JoystickReader;
import com.team766.simulator.ProgramInterface;

public class Joystick implements JoystickReader {

    private final ProgramInterface.Joystick source;
    private final Companion companion;

    public Joystick(RulesMixin oi, int index) {
        source = ProgramInterface.joystickChannels[index];
        companion = new Companion(this, oi);
    }

    @Override
    public Companion getCompanion() {
        return companion;
    }

    @Override
    public int getMaxAxisCount() {
        return source.axisValues.length;
    }

    @Override
    public double getAxis(final int axis) {
        return source.axisValues[axis];
    }

    @Override
    public int getMaxButtonCount() {
        return source.buttonValues.length;
    }

    @Override
    public boolean getButton(final int button) {
        // Button indexes begin at 1 in WPILib, so match that here
        if (button <= 0) {
            return false;
        }
        return source.buttonValues[button - 1];
    }

    @Override
    public int getPOV() {
        return source.povValue;
    }
}
